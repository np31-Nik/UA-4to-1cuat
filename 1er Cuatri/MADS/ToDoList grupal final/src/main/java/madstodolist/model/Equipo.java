package madstodolist.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "equipos")
public class Equipo {

    private static final long serialVersionUID = 1L;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descripcion;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "equipo_usuario",
        joinColumns = { @JoinColumn(name = "fk_equipo") },
        inverseJoinColumns = {@JoinColumn(name = "fk_usuario")})
    Set<Usuario> usuarios = new HashSet<>();

    @OneToMany(mappedBy = "equipo", fetch = FetchType.EAGER)
    Set<Tarea> tareas = new HashSet<>();

    public Equipo(String _name){
        this.name = _name;
    }

    public Equipo() {
    }

    public String getNombre(){
        return name;
    }

    public void setNombre(String nombre) {this.name = nombre;}

    public Long getId(){
        return id;
    }

    public void setId(Long _id){
        this.id = _id;
    }

    public void setDesc(String d){
        descripcion = d;
    }

    public String getDesc(){
        return descripcion;
    }

    @Override
    public boolean equals (Object o){
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Equipo equipo = (Equipo) o;
        if(id != null && equipo.id != null){
            return Objects.equals(id, equipo.id);
        }
        return name.equals(equipo.name);
    }

    public Set<Usuario> getUsuarios(){
        return usuarios;
    }


    @Override
    public int hashCode(){
        return Objects.hash(name);
    }

    public void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
        usuario.getEquipos().add(this);
    }

    public void deleteUser(Usuario usuario){
        usuarios.remove(usuario);
        usuario.getEquipos().remove(this);
    }

    public Set<Tarea> getTareas() {
        return tareas;
    }

    public void setTareas(Set<Tarea> tareas) {
        this.tareas = tareas;
    }

}
