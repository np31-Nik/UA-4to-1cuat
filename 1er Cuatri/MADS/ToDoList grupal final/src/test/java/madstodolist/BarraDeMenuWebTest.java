package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpSession;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class BarraDeMenuWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    @Test
    public void BarraDeMenuSinUsuario() throws Exception{
        when(managerUserSession.usuarioLogeado()).thenReturn(null);
        String url = "/about";
        this.mockMvc.perform(get(url))
                .andExpect((content().string(containsString("Login"))));
    }

    @Test
    public void BarraDeMenuConUsuario() throws Exception{
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("user");
        usuario.setPassword("123");
        usuario.setId(1L);
        usuario = usuarioService.registrar(usuario);
        managerUserSession.logearUsuario(usuario.getId());
        when(managerUserSession.usuarioLogeado()).thenReturn(usuario.getId());

        String url = "/about";

        this.mockMvc.perform(get(url))
                .andExpect((content().string(containsString("user"))));
    }
}
