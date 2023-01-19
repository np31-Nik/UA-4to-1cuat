package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.TareaNotFoundException;
import madstodolist.controller.exception.UsuarioNoTieneDerechos;
import madstodolist.model.Categoria;
import madstodolist.model.Comentario;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.CategoriaService;
import madstodolist.service.ComentarioService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
public class ComentarioController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ComentarioService comentarioService;
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

    @GetMapping("/tareas/{idTarea}/comentarios/")
    public String formNuevoComentario(@PathVariable(value="idTarea") Long idTarea,Model model,HttpSession session, @ModelAttribute ComentarioData comentarioData) {
        Tarea tarea = tareaService.findById(idTarea);
        Usuario usuario = usuarioService.findById(managerUserSession.usuarioLogeado());
        comprobarUsuarioLogeado(usuario.getId());
        if(usuario == null ){
            throw new UsuarioNoLogeadoException();
        }
        List<Comentario> comentarios = new ArrayList<Comentario>(tarea.getComentarios());
        model.addAttribute("comentarios",comentarios);
        model.addAttribute("usuario", usuario);
        model.addAttribute("id", usuario.getId());
        model.addAttribute("tareaId", idTarea);


        return "comentariosTarea";
    }

    @PostMapping("/tareas/{idTarea}/comentarios/")
    public String nuevoComentario(@PathVariable(value="idTarea") Long idTarea, @ModelAttribute ComentarioData comentarioData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {
        Long idUsuario = (Long) managerUserSession.usuarioLogeado();
        comprobarUsuarioLogeado(idUsuario);
        Tarea tarea = tareaService.findById(idTarea);
        System.out.println(idTarea);

        System.out.println(idUsuario);
        System.out.println(comentarioData.getTexto());

        if(idTarea == null || idUsuario == null ||comentarioData.getTexto() == null){
            throw new RuntimeException();
        }
        Comentario c = comentarioService.nuevoComentario(idUsuario,idTarea,comentarioData.getTexto(), new Date());

        flash.addFlashAttribute("mensaje", "Comentario creado correctamente");

        return "redirect:/tareas/"+ idTarea + "/comentarios/";
    }



    @GetMapping("/tareas/{idTarea}/comentarios/{idComentario}/")
    public String borrarComentario(@PathVariable(value="id") Long idTarea, @PathVariable(value="idComentario") Long idComentario,
                              RedirectAttributes flash, HttpSession session) {
        Tarea tarea = tareaService.findById(idTarea);
        Long idUsuario = tarea.getUsuario().getId();
        Comentario comentario = comentarioService.findById(idComentario);
        if(!usuarioService.findById(managerUserSession.usuarioLogeado()).getAdmin()){
            comprobarUsuarioLogeado(comentario.getUsuario().getId());
        }

        if(idUsuario != comentario.getUsuario().getId() ){
            if(!usuarioService.findById(idUsuario).getAdmin()){
                throw new UsuarioNoTieneDerechos();
            }
        }

        if (tarea == null) {
            throw new TareaNotFoundException();
        }

        if (comentario == null) {
            throw new TareaNotFoundException();
        }

        comentarioService.borrarComentario(idComentario);

        return "redirect:/tareas/"+ idTarea + "/comentarios/";
    }
}

