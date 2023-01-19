package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Comentario;
import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.ComentarioService;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.checkerframework.checker.units.qual.A;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class ComentarioWebTest {

    @MockBean
    ManagerUserSession managerUserSession;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EquipoService equipoService;

    @Autowired
    TareaService tareaService;

    @Autowired
    ComentarioService comentarioService;

    Long addUsuarioBD() {
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    @Test
    public void getComentarios() throws Exception {
        Long idUser = addUsuarioBD();
        equipoService.crearEquipo("Ejemplo");
        Tarea t = tareaService.nuevaTareaUsuario(idUser,"Tarea 1", "2020-2-2", 1);
        Comentario c = comentarioService.nuevoComentario(idUser,t.getId(),"Comentario 1",new Date());

        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);

        this.mockMvc.perform(get("/tareas/"+t.getId()+"/comentarios/"))
                .andExpect(content().string(containsString("Comentario 1")));
    }
}
