package madstodolist;


import madstodolist.model.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class CategoriaTest {

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    TareaRepository tareaRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Test
    public void crearCategoria() {
        Usuario usuario = new Usuario("juan.gutierrez@gmail.com");

        Categoria cat = new Categoria(usuario,"Cat1","#FFFFFF");

        assertThat(cat.getNombre()).isEqualTo("Cat1");
        assertThat(cat.getColor()).isEqualTo("#FFFFFF");
        assertThat(cat.getUsuario()).isEqualTo(usuario);
    }

    @Test
    @Transactional
    public void guardarCategoriaEnBaseDatos() {
        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);

        Categoria cat = new Categoria(usuario,"Cat1","#FFFFFF");

        categoriaRepository.save(cat);

        assertThat(cat.getId()).isNotNull();

        Categoria catBD = categoriaRepository.findById(cat.getId()).orElse(null);
        assertThat(catBD.getNombre()).isEqualTo(cat.getNombre());
        assertThat(catBD.getColor()).isEqualTo(cat.getColor());
        assertThat(catBD.getUsuario()).isEqualTo(cat.getUsuario());
    }

    @Test
    @Transactional
    public void unUsuarioTieneUnaListaDeCategorias() {

        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();

        Categoria cat1 = new Categoria(usuario,"Cat1","#FFFFFF");
        Categoria cat2 = new Categoria(usuario,"Cat2","#FAFFFF");
        categoriaRepository.save(cat1);
        categoriaRepository.save(cat2);

        Usuario usuarioRecuperado = usuarioRepository.findById(usuarioId).orElse(null);

        assertThat(usuarioRecuperado.getCategorias()).hasSize(2);
    }

    @Test
    @Transactional
    public void añadirUnaCategoriaAUnUsuarioEnBD() {

        Usuario usuario = new Usuario("user@ua");
        usuarioRepository.save(usuario);
        Long usuarioId = usuario.getId();

        Usuario usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        Categoria cat1 = new Categoria(usuario,"Cat1","#FFFFFF");
        categoriaRepository.save(cat1);


        Categoria catBD = categoriaRepository.findById(cat1.getId()).orElse(null);
        assertThat(catBD.getNombre()).isEqualTo(cat1.getNombre());
        assertThat(catBD.getUsuario()).isEqualTo(usuarioBD);

        usuarioBD = usuarioRepository.findById(usuarioId).orElse(null);
        assertThat(usuarioBD.getCategorias()).contains(catBD);
    }
}
