package madstodolist.controller;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.controller.event.OnRegistrationSuccessEvent;
import madstodolist.controller.event.OnRegistrationSuccessPublisher;
import madstodolist.controller.exception.UsuarioBloqueadoException;
import madstodolist.model.Usuario;
import madstodolist.model.VerificationToken;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import madstodolist.service.VerificationTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Calendar;

@Controller
public class LoginController {

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    VerificationTokenService verificationTokenService;

    @Autowired
    ManagerUserSession managerUserSession;

    @Autowired
    OnRegistrationSuccessPublisher onRegistrationSuccessPublisher;

    @GetMapping("/")
    public String home(Model model) {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginData", new LoginData());
        return "formLogin";
    }

    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute LoginData loginData, Model model, HttpSession session) {

        // Llamada al servicio para comprobar si el login es correcto
        UsuarioService.LoginStatus loginStatus = usuarioService.login(loginData.geteMail(), loginData.getPassword());

        if (loginStatus == UsuarioService.LoginStatus.LOGIN_OK) {
            Usuario usuario = usuarioService.findByEmail(loginData.geteMail());

            if(usuario.getBloqueado()){
                throw new UsuarioBloqueadoException();
            }
            managerUserSession.logearUsuario(usuario.getId());

            if(usuario.getAdmin()){
                return "redirect:/registrados";
            }



            return "redirect:/usuarios/" + usuario.getId() + "/tareas";
        } else if (loginStatus == UsuarioService.LoginStatus.USER_NOT_FOUND) {
            model.addAttribute("error", "No existe usuario");
            return "formLogin";
        } else if (loginStatus == UsuarioService.LoginStatus.ERROR_PASSWORD) {
            model.addAttribute("error", "Contraseña incorrecta");
            return "formLogin";
        }
        return "formLogin";
    }

    @GetMapping("/registro")
    public String registroForm(Model model) {
        model.addAttribute("registroData", new RegistroData());
        boolean existeAdmin = usuarioService.existeAdmin();
        model.addAttribute("existeAdmin", existeAdmin);
        return "formRegistro";
    }

   @PostMapping("/registro")
   public String registroSubmit(@Valid RegistroData registroData, BindingResult result, WebRequest request, Model model) {

        if (result.hasErrors()) {
            return "formRegistro";
        }

        if (usuarioService.findByEmail(registroData.geteMail()) != null) {
            model.addAttribute("registroData", registroData);
            model.addAttribute("error", "El usuario " + registroData.geteMail() + " ya existe");
            return "formRegistro";
        }

        Usuario usuario = new Usuario(registroData.geteMail());
        usuario.setPassword(registroData.getPassword());
        usuario.setFechaNacimiento(registroData.getFechaNacimiento());
        usuario.setNombre(registroData.getNombre());
        usuario.setAdmin(registroData.getAdmin());
        //por defecto esta bloqueado hasta que se confirme el registro
        usuario.setBloqueado(true);

        usuarioService.registrar(usuario);

        try {
            String url = request.getContextPath();
            onRegistrationSuccessPublisher.publishEvent(usuario, url);

        } catch (Exception me) {

            return "formRegistro";
        }

        //Token de verificación enviado correctamente
        model.addAttribute("info", "Se ha enviado un email de confirmación, revise su bandeja de entrada para activar su cuenta");
        return loginForm(model);
   }

    @GetMapping("/confirmarRegistro")
    public String confirmRegistration(WebRequest request, Model model,@RequestParam String token) {
        VerificationToken verificationToken = verificationTokenService.getVerificationToken(token);
        if(verificationToken == null) {
            // No existe token
            model.addAttribute("error", "Token no válido");
            return loginForm(model);
        }
        Usuario user = verificationToken.getUsuario();

        Calendar calendar = Calendar.getInstance();
        if((verificationToken.getExpiryDate().getTime()-calendar.getTime().getTime())<=0) {
            // Token caducado
            model.addAttribute("error", "Token de verificación caducado");
            return loginForm(model);
        }
        //activamos al usuario
        usuarioService.bloquearUsuario(user.getId());
        model.addAttribute("success", "Usuario activado correctamente, ya puede iniciar sesión");
        return loginForm(model);
    }

   @GetMapping("/logout")
   public String logout(HttpSession session) {
        managerUserSession.logout();
        return "redirect:/login";
   }
}
