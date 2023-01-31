package madstodolist.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class UsuarioData {

    private String nombre;
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date fechaNacimiento;

    private String contrasenyaActual;
    private String contrasenyaNueva;
    private String contrasenyaConfrimacion;


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getContrasenyaActual(){ return this.contrasenyaActual;}
    public void setContrasenyaActual(String contrasenya){this.contrasenyaActual = contrasenya;}

    public String getContrasenyaNueva(){ return this.contrasenyaNueva;}
    public void setContrasenyaNueva(String contrasenya){this.contrasenyaNueva = contrasenya;}

    public String getContrasenyaConfirmacion(){ return this.contrasenyaConfrimacion;}
    public void setContrasenyaConfirmacion(String contrasenya){this.contrasenyaConfrimacion = contrasenya;}






}
