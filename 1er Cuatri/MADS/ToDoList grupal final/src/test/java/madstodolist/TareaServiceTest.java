package madstodolist;

import madstodolist.model.Equipo;
import madstodolist.model.Tarea;
import madstodolist.model.Usuario;
import madstodolist.service.EquipoService;
import madstodolist.service.TareaService;
import madstodolist.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;

// Hemos eliminado todos los @Transactional de los tests
// y usado un script para limpiar la BD de test después de
// cada test
// https://dev.to/henrykeys/don-t-use-transactional-in-tests-40eb

@SpringBootTest
@Sql(scripts = "/clean-db.sql", executionPhase = AFTER_TEST_METHOD)
public class TareaServiceTest {

    @Autowired
    UsuarioService usuarioService;
    @Autowired
    EquipoService equipoService;
    @Autowired
    TareaService tareaService;

    class DosIds {
        Long usuarioId;
        Long tareaId;
        public DosIds(Long usuarioId, Long tareaId) {
            this.usuarioId = usuarioId;
            this.tareaId = tareaId;
        }
    }

    // Método para inicializar los datos de prueba en la BD
    // Devuelve el identificador del usuario y el de la primera tarea añadida
    DosIds addUsuarioTareasBD() {
        Usuario usuario = new Usuario("user@ua");
        usuario.setPassword("123");
        usuario = usuarioService.registrar(usuario);
        Tarea tarea1 = tareaService.nuevaTareaUsuario(usuario.getId(), "Lavar coche", null, 0);
        tareaService.nuevaTareaUsuario(usuario.getId(), "Renovar DNI", null, 0);
        return new DosIds(usuario.getId(), tarea1.getId());
    }

    @Test
    public void testNuevaTareaUsuario() {
        // GIVEN
        // Un usuario en la BD

        Long usuarioId = addUsuarioTareasBD().usuarioId;

        // WHEN
        // creamos una nueva tarea asociada al usuario,
        Tarea tarea = tareaService.nuevaTareaUsuario
                (usuarioId, "Práctica 5 de MADS", "12-21-2022", 0);

        // THEN
        // al recuperar el usuario usando el método findByEmail la tarea creada
        // está en la lista de tareas del usuario.

        Usuario usuario = usuarioService.findByEmail("user@ua");
        assertThat(usuario.getTareas()).hasSize(3);
        assertThat(usuario.getTareas()).contains(tarea);
                
    }

    @Test
    public void testBuscarTarea() {
        // GIVEN
        // Una tarea en la BD

        Long tareaId = addUsuarioTareasBD().tareaId;

        // WHEN
        // recuperamos una tarea de la base de datos a partir de su ID,

        Tarea lavarCoche = tareaService.findById(tareaId);

        // THEN
        // los datos de la tarea recuperada son correctos.

        assertThat(lavarCoche).isNotNull();
        assertThat(lavarCoche.getTitulo()).isEqualTo("Lavar coche");
    }

    @Test
    public void testModificarTarea() {
        // GIVEN
        // Un usuario y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        // WHEN
        // modificamos la tarea correspondiente a ese identificador,

        tareaService.modificaTarea(tareaId, "Limpiar los cristales del coche", "12-25-2022", 3);

        // THEN
        // al buscar por el identificador en la base de datos se devuelve la tarea modificada

        Tarea tareaBD = tareaService.findById(tareaId);
        assertThat(tareaBD.getTitulo()).isEqualTo("Limpiar los cristales del coche");
        assertThat(tareaBD.getFechaTope()).isEqualTo("12-25-2022");
        assertThat(tareaBD.getPrioridad()).isEqualTo(3);



        // y el usuario tiene también esa tarea modificada.
        Usuario usuarioBD = usuarioService.findById(usuarioId);
        usuarioBD.getTareas().contains(tareaBD);
    }

    @Test
    public void testBorrarTarea() {
        // GIVEN
        // Un usuario y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        // WHEN
        // borramos la tarea correspondiente al identificador,

        tareaService.borraTarea(tareaId);

        // THEN
        // la tarea ya no está en la base de datos ni en las tareas del usuario.

        assertThat(tareaService.findById(tareaId)).isNull();
        assertThat(usuarioService.findById(usuarioId).getTareas()).hasSize(1);
    }

    @Test
    public void testFinalizarTarea() {
        // GIVEN
        // Un usuario y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        // WHEN
        // finalizamos la tarea correspondiente al identificador,

        tareaService.finalizarTarea(tareaId);

        // THEN
        // la tarea ya está marcada en la base de datos como terminada.

        assertThat(tareaService.findById(tareaId).getTerminado());
        assertThat(usuarioService.findById(usuarioId).getTareas()).hasSize(2);
    }

    @Test
    public void testRestablecerTarea() {
        // GIVEN
        // Un usuario y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        // WHEN
        // finalizamos la tarea correspondiente al identificador,

        tareaService.finalizarTarea(tareaId);
        tareaService.restablecerTarea(tareaId);

        // THEN
        // la tarea ya está restablecida.

        assertThat(!tareaService.findById(tareaId).getTerminado());
        assertThat(usuarioService.findById(usuarioId).getTareas()).hasSize(2);
    }

    @Test
    public void testAsignarEquipoTarea(){
        // GIVEN
        // Un equipo y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        Long equipoId = equipoService.crearEquipo("Equipo 1").getId();

        // WHEN
        // asignamos un equipo a la tarea correspondiente al identificador,

        tareaService.addEquipoTarea(tareaId, equipoId);

        // THEN
        // la tarea ya está asignada a un equipo.
        Equipo equipo = tareaService.findById(tareaId).getEquipo();

        assertThat(equipo).isNotNull();
        assertThat(equipo.getNombre()).isEqualTo("Equipo 1");


    }

    @Test
    public void testModificarColor() {
        // GIVEN
        // Un usuario y una tarea en la BD

        DosIds dosIds = addUsuarioTareasBD();
        Long usuarioId = dosIds.usuarioId;
        Long tareaId = dosIds.tareaId;

        // modificamos la tarea correspondiente a ese identificador,
        String color = "#555555";
        tareaService.modificarColor(tareaId, color);

        // THEN
        // al buscar por el identificador en la base de datos se devuelve la tarea modificada

        Tarea tareaBD = tareaService.findById(tareaId);
        assertThat(tareaBD.getColor()).isEqualTo(color);
    }
}
