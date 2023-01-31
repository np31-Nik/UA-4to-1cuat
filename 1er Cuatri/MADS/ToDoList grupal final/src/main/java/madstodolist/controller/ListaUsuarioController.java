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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class ListaUsuarioController {

    List<Usuario> listado;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    ManagerUserSession managerUserSession;

    Usuario usuario;

    public boolean usuarioEsAdmin(){
        Long idUsuario = (Long) managerUserSession.usuarioLogeado();
        if (idUsuario != null){
           this.usuario = usuarioService.findById(idUsuario);
           if (this.usuario != null){
               return usuario.getAdmin();

           }
           else {
               return false;
           }
        }
        return false;
    }


    @GetMapping("/registrados")
    public String registrados(Model model){
        boolean admin = usuarioEsAdmin();
        if (admin){
            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);
            listado = usuarioService.listadoUsuarios();
            model.addAttribute("usuarios", listado);
            return "listaUsuarios";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }
    }

    @GetMapping("/registrados/{idDetalle}")
    // La anotación @ResponseBody sirve para que la cadena devuelta sea la resupuesta
    // de la petición HTTP, en lugar de una plantilla thymeleaf
    public String detalleUsuario(@PathVariable(value="idDetalle") Long idDetalleUsuario, Model model, HttpSession session) {
        boolean admin = usuarioEsAdmin();
        if (admin){
            Usuario detalleUsuario = usuarioService.findById(idDetalleUsuario);

            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);

            if (detalleUsuario == null){
                throw new UsuarioNotFoundException();
            }

            model.addAttribute("detalleUsuario", detalleUsuario);
            return "detalleUsuario";
        }
        else {
            throw new UsuarioNoLogeadoException();
        }

    }


    @GetMapping("/bloquear/{idBloquear}")
    public String bloquearUsuario(@PathVariable(value="idBloquear") Long idBloquearUsuario, Model model, HttpSession session){
        boolean admin = usuarioEsAdmin();
        if (admin){
            boolean bloqueado = usuarioService.bloquearUsuario(idBloquearUsuario);
            model.addAttribute("id", this.usuario.getId());
            model.addAttribute("usuario", this.usuario);
            listado = usuarioService.listadoUsuarios();
            model.addAttribute("usuarios", listado);
            return "redirect:/registrados";
        }
        else{
            throw new UsuarioNoLogeadoException();
        }
    }
}
