package madstodolist.service;

import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TareaService {

    Logger logger = LoggerFactory.getLogger(TareaService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private EquipoRepository equipoRepository;
    @Autowired
    private TareaRepository tareaRepository;

    @Transactional
    public Tarea nuevaTareaUsuario(Long idUsuario, String tituloTarea, String nuevaFechaTope, int prioridad) {
        logger.debug("Añadiendo tarea " + tituloTarea + " al usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear tarea " + tituloTarea);
        }
        Tarea tarea = new Tarea(usuario, tituloTarea);
        tarea.setFechaTope(nuevaFechaTope);
        tarea.setPrioridad(prioridad);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public boolean addEquipoTarea(Long idTarea, Long idEquipo){
        Tarea t = tareaRepository.findById(idTarea).orElse(null);
        logger.debug("Añadiendo equipo " + idEquipo + " a la tarea " + idTarea);
        if(t != null){
            logger.debug("Tarea " + idTarea + " encontrada");
            Equipo e = equipoRepository.findById(idEquipo).orElse(null);
            if(e != null){
                logger.debug("Equipo " + idEquipo + " encontrado");
                t.setEquipo(e);
                tareaRepository.save(t);
                return true;
            }
            else{
                List<Long> equiposids = equipoRepository.findAll().stream().map(Equipo::getId).collect(Collectors.toList());
                logger.debug("Equipo " + idEquipo + " no encontrado");
                logger.debug("Equipos disponibles: " + equiposids);
                return false;
            }
        }
        return false;
    }

    @Transactional(readOnly = true)
    public List<Tarea> allTareasUsuario(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(usuario.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return tareas;
    }

    @Transactional(readOnly = true)
    public List<Tarea> tareasNoFinalizadas(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas no finalizadas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(usuario.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        for(int i=0; i < tareas.size(); i++){
            Tarea t = tareas.get(i);
            if (t.getTerminado()){
                tareas.remove(i);
                i = i - 1;
            }
        }

        return tareas;
    }

    @Transactional(readOnly = true)
    public List<Tarea> tareasNoFinalizadasDelEquipo(Long idEquipo) {
        logger.debug("Devolviendo todas las tareas no finalizadas del equipo " + idEquipo);
        Equipo equipo = equipoRepository.findById(idEquipo).orElse(null);
        if (equipo == null) {
            throw new TareaServiceException("Usuario " + idEquipo + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(equipo.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        for(int i=0; i < tareas.size(); i++){
            Tarea t = tareas.get(i);
            if (t.getTerminado()){
                tareas.remove(i);
                i = i - 1;
            }
        }

        return tareas;
    }

    @Transactional(readOnly = true)
    public List<Tarea> tareasFinalizadas(Long idUsuario) {
        logger.debug("Devolviendo todas las tareas finalizadas del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar tareas ");
        }
        List<Tarea> tareas = new ArrayList(usuario.getTareas());
        Collections.sort(tareas, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        for(int i=0; i < tareas.size(); i++){
            Tarea t = tareas.get(i);
            if (!t.getTerminado()){
                tareas.remove(i);
                i = i - 1;
            }
        }

        return tareas;
    }

    @Transactional(readOnly = true)
    public Tarea findById(Long tareaId) {
        logger.debug("Buscando tarea " + tareaId);
        return tareaRepository.findById(tareaId).orElse(null);
    }

    @Transactional
    public Tarea modificaTarea(Long idTarea, String nuevoTitulo, String nuevaFechaTope, int prioridad) {
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoTitulo);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTitulo(nuevoTitulo);
        tarea.setFechaTope(nuevaFechaTope);
        tarea.setPrioridad(prioridad);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public void borraTarea(Long idTarea) {
        logger.debug("Borrando tarea " + idTarea);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tareaRepository.delete(tarea);
    }

    @Transactional
    public void finalizarTarea(Long idTarea){
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null){
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTerminada(true);
        tarea.setFecheTerminado(LocalDate.now().toString());
        tareaRepository.save(tarea);
    }

    @Transactional
    public void restablecerTarea(Long idTarea) {
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setTerminada(false);
        tarea.setFecheTerminado(null);
        tareaRepository.save(tarea);
    }
    @Transactional
    public Tarea modificarColor(Long idTarea, String nuevoColor){
        logger.debug("Modificando tarea " + idTarea + " - " + nuevoColor);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setColor(nuevoColor);
        tareaRepository.save(tarea);
        return tarea;
    }

    @Transactional
    public Tarea modificaCategoria(Long idTarea, Categoria cat) {
        logger.debug("Modificando categoria de tarea " + idTarea);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);
        if (tarea == null) {
            throw new TareaServiceException("No existe tarea con id " + idTarea);
        }
        tarea.setCategoria(cat);
        tareaRepository.save(tarea);
        return tarea;
    }
}
