package madstodolist;

import madstodolist.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class ComentariosTest {



        @Autowired
        UsuarioRepository usuarioRepository;

        @Autowired
        EquipoRepository equipoRepository;

        @Autowired
        TareaRepository tareaRepository;
        @Autowired
        ComentarioRepository comentarioRepository;


    @Test
    public void crearComentario() {
        // GIVEN
        // Un usuario nuevo creado en memoria, sin conexión con la BD,

        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        // WHEN
        // se crea una nueva tarea con ese usuario,

        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");
        Comentario comentario = new Comentario(usuario, tarea, "holiss", new Date());

        // THEN
        // el título y el usuario de la tarea son los correctos.

        assertThat(comentario.getTexto()).isEqualTo("holiss");
    }

    @Test
    public void guardarComentarioEnBD() {

        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        Tarea tarea = new Tarea(usuario, "Práctica 1 de MADS");

        Comentario comentario = new Comentario(usuario, tarea, "holiss", new Date());
        tareaRepository.save(tarea);
        comentarioRepository.save(comentario);

        assertThat(comentario.getId()).isNotNull();

        Comentario comentarioBD = comentarioRepository.findById(comentario.getId()).orElse(null);
        assertThat(comentarioBD.getTexto()).isEqualTo(comentario.getTexto());
    }
}
