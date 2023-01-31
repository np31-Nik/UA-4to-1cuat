package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.exception.UsuarioNoLogeadoException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.http.HttpSession;

@Controller
public class HomeController {
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ManagerUserSession managerUserSession;

    @GetMapping ("/about")
    public String about(Model model, HttpSession httpSession){
        Long idUsuario = (Long) managerUserSession.usuarioLogeado();
        //Long idUsuario = (Long) httpSession.getAttribute("idUsuarioLogeado");

        if(idUsuario != null){
            Usuario usuario = usuarioService.findById(idUsuario);
            model.addAttribute("id", idUsuario);
            model.addAttribute("usuario", usuario);
        }

        return "about";
    }

    @GetMapping ("/cuenta")
    public String cuenta(Model model, HttpSession httpSession){

        Long idUsuario = (Long) managerUserSession.usuarioLogeado();

        if(idUsuario != null){
            UsuarioData uData = new UsuarioData();

            Usuario usuario = usuarioService.findById(idUsuario);
            uData.setNombre(usuario.getNombre());
            uData.setFechaNacimiento(usuario.getFechaNacimiento());
            model.addAttribute("id", idUsuario);
            model.addAttribute("usuario", usuario);
            model.addAttribute("UsuarioData", uData);
        }else {
            return "redirect:/login";
        }

        return "perfilUsuario";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String editarPerfil(@PathVariable(value="id") Long idUsuario, @ModelAttribute UsuarioData usuarioData, Model model, HttpSession session){

        Usuario usuario = usuarioService.findById(idUsuario);

        if (usuario==null){
            throw new UsuarioNotFoundException();
        }
        if (managerUserSession.usuarioLogeado()!= idUsuario){
            throw new UsuarioNoLogeadoException();
        }

        usuarioService.modificarUsuario(idUsuario, usuarioData.getNombre(),usuarioData.getFechaNacimiento());

        return "redirect:/cuenta";

    }

    @PostMapping("/{id}/editarContrasenya")
    public String editarContrasenya(@PathVariable(value = "id") Long idUsuario, @ModelAttribute UsuarioData usuarioData, Model model, HttpSession session){

        System.out.println(usuarioData.getContrasenyaActual()+usuarioData.getContrasenyaNueva()+usuarioData.getContrasenyaConfirmacion());
        Usuario usuario = usuarioService.findById(idUsuario);
        if (usuario==null){
            throw new UsuarioNotFoundException();
        }

        if (managerUserSession.usuarioLogeado()!= idUsuario){
            throw new UsuarioNoLogeadoException();
        }

        if(!usuarioData.getContrasenyaActual().equals(usuario.getPassword())){
            model.addAttribute("error", "La contraseña actual no es la correcta" );
            return cuenta(model, session);
        }

        if(!usuarioData.getContrasenyaConfirmacion().equals(usuarioData.getContrasenyaNueva())){
            model.addAttribute("error", "Las nuevas contraseñas no coinciden" );
            return cuenta(model, session);
        }

        usuarioService.modificarContrasenya(idUsuario, usuarioData.getContrasenyaNueva());
        model.addAttribute("success", "Contraseña modificada correctamente" );

        return "redirect:/cuenta";
    }




}
