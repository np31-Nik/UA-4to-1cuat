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
public class CategoriaController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    CategoriaService categoriaService;

    @Autowired
    ManagerUserSession managerUserSession;

    private void comprobarUsuarioLogeado(Long idUsuario) {
        Long idUsuarioLogeado = managerUserSession.usuarioLogeado();
        if (!idUsuario.equals(idUsuarioLogeado))
            throw new UsuarioNoLogeadoException();
    }

    @GetMapping("/usuarios/{id}/categorias/nueva")
    public String formNuevaCategoria(@PathVariable(value="id") Long idUsuario,
                                 @ModelAttribute CategoriaData categoriaData, Model model,
                                 HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        model.addAttribute("usuario", usuario);
        return "formNuevaCategoria";
    }

    @PostMapping("/usuarios/{id}/categorias/nueva")
    public String nuevaCategoria(@PathVariable(value="id") Long idUsuario, @ModelAttribute CategoriaData categoriaData,
                             Model model, RedirectAttributes flash,
                             HttpSession session) {

        comprobarUsuarioLogeado(idUsuario);

        Usuario usuario = usuarioService.findById(idUsuario);
        Categoria cat= categoriaService.nuevaCategoriaUsuario(idUsuario, categoriaData.getNombre(), categoriaData.getColor());
        flash.addFlashAttribute("mensaje", "Categoria creada correctamente");
        return "redirect:/usuarios/" + idUsuario + "/tareas";
    }
}

