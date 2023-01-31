package madstodolist.controller.event;

import madstodolist.model.Usuario;
import org.springframework.context.ApplicationEvent;

public class OnRegistrationSuccessEvent extends ApplicationEvent{
    private static final long serialVersionUID = 1L;
    private String appUrl;

    private Usuario user;

    public OnRegistrationSuccessEvent(Usuario user, String appUrl){

        super(user);
        this.user = user;
        this.appUrl = appUrl;
        System.out.println("OnRegistrationSuccessEvent");
    }

    public String getAppUrl(){
        return this.appUrl;
    }

    public void setAppUrl(String appUrl){
        this.appUrl = appUrl;
    }

    public Usuario getUsuario(){
        return this.user;

    }
    public void setUsuario(Usuario user){
        this.user = user;
    }



}
