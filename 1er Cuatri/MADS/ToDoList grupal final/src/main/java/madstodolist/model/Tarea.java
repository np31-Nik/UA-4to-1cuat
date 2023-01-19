package madstodolist.model;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tareas")
public class Tarea implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String titulo;

    private String color;


    @NotNull
    // Relación muchos-a-uno entre tareas y usuario
    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del usuario con el que está asociado una tarea
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    //tareas al equipo
    @ManyToOne
    @JoinColumn(name = "equipo_id")
    private Equipo equipo;


    private String fechaTope;

    private String fecheTerminado;

    private boolean terminada;


    private int prioridad;


    @ManyToOne
    // Nombre de la columna en la BD que guarda físicamente
    // el ID del categoria con el que está asociado una tarea
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @OneToMany(mappedBy = "tarea", fetch = FetchType.EAGER)
    Set<Comentario> comentarios = new HashSet<>();


    // Constructor vacío necesario para JPA/Hibernate.
    // No debe usarse desde la aplicación.
    public Tarea() {}

    // Al crear una tarea la asociamos automáticamente a un
    // usuario. Actualizamos por tanto la lista de tareas del
    // usuario.
    public Tarea(Usuario usuario, String titulo) {
        this.usuario = usuario;
        this.titulo = titulo;
        usuario.getTareas().add(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Equipo getEquipo() {
        return equipo;
    }

    public void setEquipo(Equipo equipo) {
        this.equipo = equipo;
    }

    public String getFechaTope(){

        return fechaTope;
    }
    public void setFechaTope(String newFecha){
        fechaTope = newFecha;
    }

    public String getFechaTerminado(){
        return fecheTerminado;
    }
    public void setFecheTerminado(String newFecha){
        fecheTerminado = newFecha;
    }

    public boolean getTerminado(){
        return terminada;
    }
    public void setTerminada(boolean newTerminada){
        terminada = newTerminada;
    }


    public int getPrioridad(){
        return prioridad;
    }

    public void setPrioridad(int newPrioridad){
        prioridad = newPrioridad;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public Set<Comentario> getComentarios(){return comentarios;}

    public void setComentarios(Set<Comentario> comentarios){this.comentarios = comentarios;}





    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        if (id != null && tarea.id != null)
            // Si tenemos los ID, comparamos por ID
            return Objects.equals(id, tarea.id);
        // sino comparamos por campos obligatorios
        return titulo.equals(tarea.titulo) &&
                usuario.equals(tarea.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titulo, usuario);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String c){
        color=c;
    }
}
