package madstodolist.controller.event;

import madstodolist.model.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class OnRegistrationSuccessPublisher {
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void publishEvent(Usuario user, String appUrl){
        System.out.println("Enviando evento de registro");
        OnRegistrationSuccessEvent event = new OnRegistrationSuccessEvent(user, appUrl);
        eventPublisher.publishEvent(event);
    }
}
