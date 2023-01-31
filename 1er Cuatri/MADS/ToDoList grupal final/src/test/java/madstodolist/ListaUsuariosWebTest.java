package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Usuario;
import madstodolist.service.UsuarioService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class ListaUsuariosWebTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioService usuarioService;

    @MockBean
    private ManagerUserSession managerUserSession;

    Long addUsuarioBD() {
        Usuario usuario = new Usuario("user1@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    Long addUsuarioAdminBD() {
        Usuario usuario = new Usuario("admin@ua");
        usuario.setNombre("Usuario Administrador");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    Long addUsuarioBloqueadoBD() {
        Usuario usuario = new Usuario("bloqueado@ua");
        usuario.setNombre("Usuario bloqueado");
        usuario.setPassword("123");
        usuario.setAdmin(false);
        usuario.setBloqueado(true);
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }

    @Test
    public void getRegistradosDevuelveNombreAplicacion() throws Exception {
        Long idAdmin = addUsuarioAdminBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);
        this.mockMvc.perform(get("/registrados"))
                .andExpect(content().string(containsString("Usuarios Registrados")));
    }

    @Test
    public void getRegistradosContieneUnUsuario() throws Exception{
        Long user1 = addUsuarioBD();
        Long idAdmin = addUsuarioAdminBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(content().string(containsString("user1@ua")));

    }

    @Test
    public void getDetalleDeUsuario() throws Exception{
        Long user = addUsuarioBD();
        Long idAdmin = addUsuarioAdminBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        this.mockMvc.perform(get("/registrados/"+ user.toString()))
                .andExpect(content().string(containsString("Usuario Ejemplo")));
    }

    @Test
    public void getDetalleUsuarioNoExiste() throws Exception{
        Long idAdmin = addUsuarioAdminBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idAdmin);

        this.mockMvc.perform(get("/registrados/404"))
            .andExpect(status().isNotFound());
    }

    @Test
    public void usuarioNoLogeadoAccede() throws Exception {
        Long user1 = addUsuarioBD();

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
        this.mockMvc.perform(get("/registrados/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioNoAdminAccede() throws Exception {
        Long user = addUsuarioBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(user);

        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isUnauthorized());
        this.mockMvc.perform(get("/registrados/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void usuarioAdminAccede() throws Exception{
        Long admin = addUsuarioAdminBD();
        Long user = addUsuarioBD(); 
        when(managerUserSession.usuarioLogeado()).thenReturn(admin);
        this.mockMvc.perform(get("/registrados"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/registrados/"+user))
                .andExpect(status().isOk());
    }

    @Test
    public void existenUsuariosBloqueados() throws Exception {
        Long admin = addUsuarioAdminBD();
        Long user = addUsuarioBD();
        Long bloqueado = addUsuarioBloqueadoBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(admin);

        this.mockMvc.perform(get("/registrados/"+bloqueado))
                .andExpect(content().string(containsString("Desbloquear")));
        this.mockMvc.perform(get("/registrados/"+user))
                .andExpect(content().string(containsString("Bloquear")));
    }

    @Test
    public void enlaceRegistrados() throws Exception {
        Long admin = addUsuarioAdminBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(admin);
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(containsString("Registrados")));
    }

    @Test
    public void noMostrarEnlaceRegistrados() throws Exception {
        Long user = addUsuarioBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(user);
        this.mockMvc.perform(get("/about"))
                .andExpect(content().string(doesNotContainString("Registrados")));
    }
}
