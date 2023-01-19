package madstodolist;

import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.model.VerificationToken;
import madstodolist.service.UsuarioService;
import madstodolist.service.VerificationTokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class VerificationTokenTest {


    @Autowired
    private MockMvc mockMvc;


    @Autowired
    UsuarioService usuarioService;

    @Autowired
    VerificationTokenService verificationTokenService;


    @Test
    public void loginUsuarioSinVerificarKO() throws Exception {


        // GIVEN
        // Un usuario nuevo creado en memoria, sin conexión con la BD,
        Usuario usuario = new Usuario("test@test");
        usuario.setPassword("1234");
        usuarioService.registrar(usuario);

        // WHEN
        // se hace login con ese usuario,
        this.mockMvc.perform(post("/login")
                        .param("eMail", "test@test")
                        .param("password", "1234"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    public void loginUsuarioVerificadoOK() throws Exception {
        // GIVEN
        // Un usuario nuevo creado en memoria, sin conexión con la BD,
        Usuario usuario = new Usuario("test@test");
        usuario.setPassword("1234");
        usuarioService.registrar(usuario);
        VerificationToken token = new VerificationToken("1234", usuario);
        verificationTokenService.saveToken(token);

        // WHEN
        // se hace get a la url de verificación,
        this.mockMvc.perform(get("/confirmarRegistro")
                        .param("token", "1234"))
                .andExpect(status().is2xxSuccessful());

    }





}

