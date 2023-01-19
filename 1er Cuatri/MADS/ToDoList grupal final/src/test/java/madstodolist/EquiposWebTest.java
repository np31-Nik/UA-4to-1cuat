package madstodolist;

import madstodolist.authentication.ManagerUserSession;
import madstodolist.model.Equipo;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
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

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class EquiposWebTest {

    @MockBean
    ManagerUserSession managerUserSession;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UsuarioService usuarioService;

    @Autowired
    EquipoService equipoService;

    Long addUsuarioBD() {
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    Long addUsuarioAdminDB() {
        Usuario usuario = new Usuario("user1@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario.setAdmin(true);
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    Long addEquipoBD() {
        Equipo equipo = equipoService.crearEquipo("Equipo Ejemplo");
        return equipo.getId();
    }

    private Matcher<String> doesNotContainString(String s) {
        return CoreMatchers.not(containsString(s));
    }

    @Test
    public void getEquipos() throws Exception {
        Long idUser = addUsuarioBD();
        equipoService.crearEquipo("Ejemplo");
        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);

        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Equipos Disponibles")));
    }

    @Test
    public void equipoExiste() throws Exception {
        Long idUser = addUsuarioBD();
        addEquipoBD();
        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);
        this.mockMvc.perform(get("/equipos"))
                .andExpect(content().string(containsString("Equipo Ejemplo")));
    }

    @Test
    public void equipoContieneUsuarios() throws Exception {
        Long idUser = addUsuarioBD();
        Long idEquipo = addEquipoBD();
        equipoService.addUsuarioEquipo(idUser, idEquipo);

        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);
        this.mockMvc.perform(get("/equipos/"+ idEquipo))
                .andExpect(content().string(containsString("user@ua")));
    }

    @Test
    public void salirDeEquipo() throws Exception {
        Long idUser = addUsuarioBD();
        Long idEquipo = addEquipoBD();
        equipoService.addUsuarioEquipo(idUser, idEquipo);

        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);


        this.mockMvc.perform(get("/equipos/"+ idEquipo+ "/salir"));
        this.mockMvc.perform(get("/equipos/"+ idEquipo))
                .andExpect(content().string(containsString("No hay usuarios")));
    }

    @Test
    public void unirseAlEquipo() throws Exception {
        Long idUser = addUsuarioBD();
        Long idEquipo = addEquipoBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(idUser);

        this.mockMvc.perform(get("/equipos/"+ idEquipo + "/entrar"));
        this.mockMvc.perform(get("/equipos/"+ idEquipo))
                .andExpect(content().string(containsString("user@ua")));
    }


    @Test
    public void eliminarEquipo() throws Exception {
        Long admin = addUsuarioAdminDB();
        Long equipo = addEquipoBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(admin);

        this.mockMvc.perform(get("/equipos")).andExpect(content().string(containsString("Eliminar")));
        this.mockMvc.perform(get("/equipos/"+ equipo + "/eliminar")).andExpect(status().isFound());
        this.mockMvc.perform(get("/equipos/"))
                .andExpect(content().string(containsString("Todavia no hay ningun equipo")));
    }

    @Test
    public void gestionEquiposUsuarioNoAdmin() throws Exception {
        Long user = addUsuarioBD();
        Long equipo = addEquipoBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(user);

        this.mockMvc.perform(get("/equipos")).andExpect(content().string(doesNotContainString("Eliminar")));
        this.mockMvc.perform(get("/equipos/"+ equipo)).andExpect(status().isOk());
        this.mockMvc.perform(get("/equipos/"))
                .andExpect(content().string(doesNotContainString("Cambiar Nombre")));
        this.mockMvc.perform(post("/equipos/"+ equipo )
                .param("nombre", "Equipo con nombre cambiado"))
                .andExpect(status().isForbidden());
        this.mockMvc.perform(get("/equipos/"+ equipo + "/eliminar")).andExpect(status().isForbidden());
    }

    @Test
    public void cambiarNombreEquipo() throws Exception {
        Long admin = addUsuarioAdminDB();
        Long equipo = addEquipoBD();

        when(managerUserSession.usuarioLogeado()).thenReturn(admin);
        this.mockMvc.perform(post("/equipos/"+ equipo )
                        .param("nombre", "Equipo con nombre cambiado"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/equipos"));
        this.mockMvc.perform(get("/equipos/"))
                .andExpect(content().string(containsString("Equipo con nombre cambiado")));
    }
}
