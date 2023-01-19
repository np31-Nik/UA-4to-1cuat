package madstodolist.controller;

import madstodolist.model.Categoria;
import org.springframework.format.annotation.DateTimeFormat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class TareaData {
    private String titulo;
    private String color;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private String fechaTope;


    private int prioridad;

    private Categoria categoria;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getFechaTope(){
        return fechaTope;
    }
    public void setFechaTope(String newFechaTope) throws ParseException {
        fechaTope = newFechaTope;
    }


    public int getPrioridad(){
        return prioridad;
    }

    public void setPrioridad(int newPrioridad){
        prioridad = newPrioridad;
    }


    public String getColor(){return color;}

    public void setColor(String c){color=c;}

    public Categoria getCategoria(){return categoria;}

    public void setCategoria(Categoria c){categoria=c;}

}



