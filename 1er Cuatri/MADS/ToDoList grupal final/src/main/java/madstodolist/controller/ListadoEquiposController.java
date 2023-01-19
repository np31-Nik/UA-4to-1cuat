package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNoTieneDerechos;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.List;

@Controller
public class ListadoEquiposController {

    List<Equipo> listado;

    Usuario usuario;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    EquipoService equipoService;
    @Autowired
    TareaService tareaService;

    @Autowired
    UsuarioService usuarioService;

    @GetMapping("/equipos")
    public String equiposList(Model model){

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();
        if(idUsuario != null){
            this.usuario = usuarioService.findById(idUsuario);
            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);
            listado = equipoService.findAllOrderedByName();
            model.addAttribute("listado",listado);
            model.addAttribute("equipoData", new EquipoData());
            return "equiposListado";
        }
        else{
            throw new UsuarioNoLogeadoException();
        }

    }

    @GetMapping("/equipos/{idDetalle}")
    public String detalleEquipo(@PathVariable(value="idDetalle") Long idDetalleEquipo, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario != null){
            boolean usuarioPerteneceAlEquipo = false;
            this.usuario = usuarioService.findById(idUsuario);



            Equipo e = equipoService.recuperarEquipo(idDetalleEquipo);

            if (e == null){
                throw new EquipoNotFoundException();
            }

            List<Usuario> part = equipoService.usuariosEquipo(e.getId());
            if (part.contains(this.usuario)){
                usuarioPerteneceAlEquipo = true;
            }

            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);
            model.addAttribute("idDetalleEquipo", idDetalleEquipo);
            model.addAttribute("equipoData", new EquipoData());
            model.addAttribute("UsuarioPerteneceAlEquipo", usuarioPerteneceAlEquipo);
            model.addAttribute("participantes", part);
            model.addAttribute("equipoDesc",e.getDesc());
            model.addAttribute("tareas",tareaService.tareasNoFinalizadasDelEquipo(idDetalleEquipo));
            model.addAttribute("tareaData", new TareaData());
            return "detalleEquipo";
        }
        else{
            throw new UsuarioNoLogeadoException();
        }

    }

    @GetMapping("/equipos/{idDetalle}/salir")
    public String salir(@PathVariable(value="idDetalle") Long idDetalleEquipo, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();
        if(idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }

        this.usuario = usuarioService.findById(idUsuario);
        Equipo e = equipoService.recuperarEquipo(idDetalleEquipo);

        //Si el usuario no esta en el equipo no vamos a llamar la funcion eliminar usuario
        if(!equipoService.usuariosEquipo(e.getId()).contains(this.usuario)){
            return "redirect:/equipos/"+idDetalleEquipo.toString();
        }

        equipoService.eliminarUsuario(idUsuario, e.getId());
        return "redirect:/equipos/"+idDetalleEquipo.toString();
    }

    @GetMapping("/equipos/{idDetalle}/entrar")
    public String entrar(@PathVariable(value="idDetalle") Long idDetalleEquipo, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
        this.usuario = usuarioService.findById(idUsuario);



        Equipo e = equipoService.recuperarEquipo(idDetalleEquipo);
        //Si el usuario ya esta en el equipo no vamos a llamar la funcion de añadir usuario
        if(equipoService.usuariosEquipo(e.getId()).contains(this.usuario)){
            return "redirect:/equipos/"+idDetalleEquipo.toString();

        }

        equipoService.addUsuarioEquipo(idUsuario, e.getId());
        return "redirect:/equipos/"+idDetalleEquipo.toString();

    }

    @GetMapping("/equipos/{idDetalle}/eliminar")
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String eliminar(@PathVariable(value="idDetalle") Long idDetalleEquipo, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario == null) {
            throw new UsuarioNoLogeadoException();
        }
        if(!usuarioService.findById(idUsuario).getAdmin()){
            throw new UsuarioNoTieneDerechos();
        }
        this.usuario = usuarioService.findById(idUsuario);
        equipoService.eliminarEquipo(idDetalleEquipo);
        return "redirect:/equipos/";

    }

    @PostMapping ("/equipos/{idDetalle}")
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String cambiarNombre(@PathVariable(value="idDetalle") Long idDetalleEquipo, @Valid EquipoData equipoData, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();



        if(idUsuario == null){
            throw new UsuarioNoLogeadoException();
        }
        this.usuario = usuarioService.findById(idUsuario);
        if(!usuario.getAdmin()){
            throw new UsuarioNoTieneDerechos();
        }

        model.addAttribute("id", this.usuario.getId());
        model.addAttribute("usuario", this.usuario);

        Equipo equipo = equipoService.recuperarEquipo(idDetalleEquipo);
        equipoService.cambiarNombre(idDetalleEquipo, equipoData.getNombre());

        return "redirect:/equipos";

    }


    @PostMapping ("/equipos")
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String nuevoEquipo(@Valid EquipoData equipoData, Model model, HttpSession session) {

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario != null){
            this.usuario = usuarioService.findById(idUsuario);
            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);

            Equipo equipo = equipoService.crearEquipo(equipoData.getNombre());
            //equipo.addUsuario(this.usuario);
            equipoService.cambiarDesc(equipo.getId(),equipoData.getDesc());

            return "redirect:/equipos";
        }
        else{
            throw new UsuarioNoLogeadoException();
        }

    }

    @PostMapping ("/equipos/{idDetalle}/tareas")
    public String nuevaTarea(@PathVariable(value="idDetalle") Long idDetalleEquipo, @Valid TareaData tareaData, Model model, HttpSession session) {


        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario != null){
            this.usuario = usuarioService.findById(idUsuario);
            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);
            System.out.println("\n\n\n\nNueva tarea\n\n\n\n");
            Equipo equipo = equipoService.recuperarEquipo(idDetalleEquipo);
            Tarea tarea = tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo(), tareaData.getFechaTope(), tareaData.getPrioridad());
            System.out.println("\n\n\n\n"+tarea.getTitulo()+"\n\n\n\n");
           tareaService.addEquipoTarea(tarea.getId(), equipo.getId());

            return "redirect:/equipos/"+idDetalleEquipo.toString();
        }
        else{
            throw new UsuarioNoLogeadoException();
        }

    }

}
