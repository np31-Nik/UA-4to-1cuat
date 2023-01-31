package madstodolist;

import madstodolist.model.Comentario;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.ComentarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class ComentarioServiceTest {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    EquipoService equipoService;
    @Autowired
    TareaService tareaService;
    @Autowired
    ComentarioService comentarioService;
    @Test
    public void testNuevoComentarioUsuario() throws Exception {
        // GIVEN
        // Un usuario en la BD

        Usuario usuario = new Usuario("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Long usuarioId = usuario.getId();
        // WHEN
        // creamos una nueva tarea asociada al usuario,
        Tarea tarea = tareaService.nuevaTareaUsuario
                (usuarioId, "Práctica 5 de MADS", "12-21-2022", 0);

        Usuario u = usuarioService.findById(usuarioId);
        Tarea t = tareaService.findById(tarea.getId());
        if (u == null || t == null){
            throw new Exception("Algo es nulo");
        }
        Comentario c = comentarioService.nuevoComentario(u.getId(),t.getId(),"Hola",new Date());

        assertThat(c.getTexto()).isEqualTo("Hola");
        assertThat(c.getUsuario().getId()).isEqualTo(usuario.getId());
        assertThat(c.getTarea().getId()).isEqualTo(tarea.getId());

    }

    @Test
    public void testBuscarComentario() {
        // GIVEN
        // Un usuario en la BD

        Usuario usuario = new Usuario("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Long usuarioId = usuario.getId();

        // WHEN
        // creamos una nueva tarea asociada al usuario,
        Tarea tarea = tareaService.nuevaTareaUsuario
                (usuarioId, "Práctica 5 de MADS", "12-21-2022", 0);


        Comentario c = comentarioService.nuevoComentario(usuario.getId(),tarea.getId(),"Hola",new Date());
        Comentario BD = comentarioService.findById(c.getId());
        // THEN
        // los datos de la tarea recuperada son correctos.

        assertThat(BD).isNotNull();
        assertThat(BD.getTexto()).isEqualTo(c.getTexto());
    }

    @Test
    public void testBorrarComentario() {
        // GIVEN
        // Un usuario y una tarea en la BD

        Usuario usuario = new Usuario("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Long usuarioId = usuario.getId();
        Tarea tarea = tareaService.nuevaTareaUsuario
                (usuarioId, "Práctica 5 de MADS", "12-21-2022", 0);
        Long tareaId = tarea.getId();
        // WHEN
        // borramos la tarea correspondiente al identificador,

        Comentario c = comentarioService.nuevoComentario(usuarioId,tareaId,"Hola",new Date());
        // THEN
        // la tarea ya no está en la base de datos ni en las tareas del usuario.
        comentarioService.borrarComentario(c.getId());
        assertThat(comentarioService.findById(c.getId())).isNull();
    }
}
