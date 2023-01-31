package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.model.Categoria;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.CategoriaService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.List;

@Controller
public class TareaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    TareaService tareaService;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/usuarios/{id}/tareas/nueva")
    public String formNuevaTarea(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute TareaData tareaData, Model model,
                                 HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        List<Categoria> cats = categoriaService.allCatUsuario(idUsuario);
        model.addAttribute("categorias",cats);

        return "formNuevaTarea";
    }

    @PostMapping("/usuarios/{id}/tareas/nueva")
    public String nuevaTarea(@PathVariable(value="id") Long idUsuario, @ModelAttribute TareaData tareaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);
        Usuario usuario = usuarioService.findById(idUsuario);
        Tarea t = tareaService.nuevaTareaUsuario(idUsuario, tareaData.getTitulo(), tareaData.getFechaTope(), tareaData.getPrioridad());
        tareaService.modificarColor(t.getId(),tareaData.getColor());
        tareaService.modificaCategoria(t.getId(),tareaData.getCategoria());
        flash.addFlashAttribute("mensaje", "Tarea creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
     }

    @GetMapping("/usuarios/{id}/tareas")
    public String listadoTareas(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        List<Tarea> tareas = tareaService.tareasNoFinalizadas(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tareas", tareas);
        return "listaTareas";
    }

    @GetMapping("/tareas/{id}/editar")
    public String formEditaTarea(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                 Model model, HttpSession session) throws ParseException {

        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuario().getId());

        model.addAttribute("tarea", tarea);
        tareaData.setTitulo(tarea.getTitulo());
        tareaData.setFechaTope(tarea.getFechaTope());
        tareaData.setColor(tarea.getColor());

        List<Categoria> cats = categoriaService.allCatUsuario(tarea.getUsuario().getId());
        model.addAttribute("categorias",cats);
        return "formEditarTarea";
    }

    @PostMapping("/tareas/{id}/editar")
    public String grabaTareaModificada(@PathVariable(value="id") Long idTarea, @ModelAttribute TareaData tareaData,
                                       Model model, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        Long idUsuario = tarea.getUsuario().getId();

        comprobarUsuarioLogeado(idUsuario);

        tareaService.modificaTarea(idTarea, tareaData.getTitulo(), tareaData.getFechaTope(), tareaData.getPrioridad());
        tareaService.modificarColor(idTarea,tareaData.getColor());
        tareaService.modificaCategoria(idTarea,tareaData.getCategoria());
        flash.addFlashAttribute("mensaje", "Tarea modificada correctamente");
        return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";
    }

    @DeleteMapping("/tareas/{id}")
    @ResponseBody
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String borrarTarea(@PathVariable(value="id") Long idTarea, RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        if(!usuarioService.findById(managerUserSession.usuarioLogeado()).getAdmin()){
            comprobarUsuarioLogeado(tarea.getUsuario().getId());
        }

        tareaService.borraTarea(idTarea);
        return "";
    }

    @GetMapping("/tareas/{id}/finalizar")
    public String finalizarTarea(@PathVariable(value="id") Long idTarea, HttpSession session, Model model) throws ParseException {

        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        if(!usuarioService.findById(managerUserSession.usuarioLogeado()).getAdmin()){
            comprobarUsuarioLogeado(tarea.getUsuario().getId());
        }
        tareaService.finalizarTarea(tarea.getId());

        if(tarea.getEquipo() != null){
            return "redirect:/equipos/" + tarea.getEquipo().getId();

        }
        else{

            return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";

        }

//        return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareas";
    }

    @GetMapping("/usuarios/{id}/tareasTerminadas")
    public String listadoFinalizados(@PathVariable(value="id") Long idUsuario, Model model, HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        List<Tarea> tareas = tareaService.tareasFinalizadas(idUsuario);
        model.addAttribute("usuario", usuario);
        model.addAttribute("tareas", tareas);
        return "listaFinalizado";
    }

    @GetMapping("/tareas/{id}/restablecer")
    public String restablecerTarea(@PathVariable(value="id") Long idTarea, HttpSession session, Model model) throws ParseException {

        Tarea tarea = tareaService.findById(idTarea);
        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        comprobarUsuarioLogeado(tarea.getUsuario().getId());
        tareaService.restablecerTarea(tarea.getId());

        return "redirect:/usuarios/" + tarea.getUsuario().getId() + "/tareasTerminadas";
    }

}

