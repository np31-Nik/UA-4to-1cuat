package madstodolist.controller;

import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class CategoriaData {
    private String nombre;
    private String color;

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getColor(){return color;}

    public void setColor(String c){color=c;}
}



