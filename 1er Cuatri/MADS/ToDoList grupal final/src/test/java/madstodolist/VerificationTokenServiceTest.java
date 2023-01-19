package madstodolist;

import madstodolist.model.Usuario;
import madstodolist.model.VerificationToken;
import madstodolist.service.UsuarioService;
import madstodolist.service.VerificationTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class VerificationTokenServiceTest {

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Autowired
    private UsuarioService usuarioService;

    Long addUsuarioBD() {
        Usuario usuario = new Usuario("user@ua");
        usuario.setNombre("Usuario Ejemplo");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        return usuario.getId();
    }

    @Test
    public void GuardarYRecuperarToken() {
        // GIVEN
        // Un token sin guardar en la BD
        Long idUsuario = addUsuarioBD();
        VerificationToken token = new VerificationToken("prueba", usuarioService.findById(idUsuario));


        //THEN
        assertThat(verificationTokenService.getVerificationToken("prueba")).isNull();

        // WHEN
        // Guardamos el token
        verificationTokenService.saveToken(token);


        // THEN
        // Comprobamos que se ha guardado correctamente
        token = verificationTokenService.getVerificationToken("prueba");
        Usuario usuario = token.getUsuario();
        assertThat(token.getToken()).isEqualTo("prueba");
        assertThat(usuario.getEmail()).isEqualTo("user@ua");

    }

    //crear token desde usuarioService
    @Test
    public void CrearTokenDesdeUsuarioService() {
        // GIVEN
        // Un usuario en la BD
        Long idUsuario = addUsuarioBD();
        Usuario usuario = usuarioService.findById(idUsuario);

        // THEN
        // Comprobamos que no tiene token
        assertThat(verificationTokenService.getVerificationToken("prueba")).isNull();

        // WHEN
        // Creamos un token para el usuario desde el usuarioService
        usuarioService.createVerificationToken(usuario, "prueba");

        // THEN
        // Comprobamos que se ha guardado correctamente
        usuario = usuarioService.findById(idUsuario);
        VerificationToken token = verificationTokenService.getVerificationToken("prueba");
        assertThat(token.getToken()).isEqualTo("prueba");
        assertThat(usuario.getEmail()).isEqualTo("user@ua");

    }


}
