package madstodolist.controller.event;

import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;

import java.util.Properties;
import java.util.UUID;

@Component
public class RegistrationEmailListener implements ApplicationListener<OnRegistrationSuccessEvent> {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void onApplicationEvent(OnRegistrationSuccessEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationSuccessEvent event) {
        System.out.println("Enviando email de confirmación de registro");
        Usuario user = event.getUsuario();
        String token = UUID.randomUUID().toString();
        usuarioService.createVerificationToken(user, token);

        String destinatario = user.getEmail();
        String asunto = "Confirmación de registro";

        String mensaje = "Para confirmar tu registro en ToDoList, haz click en el siguiente enlace: ";

        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(destinatario);
        email.setSubject(asunto);
        String url = event.getAppUrl() + "/confirmarRegistro?token=" + token;
        email.setText(mensaje+"http://localhost:8080"+url);
        mailSender.send(email);

    }







    }
