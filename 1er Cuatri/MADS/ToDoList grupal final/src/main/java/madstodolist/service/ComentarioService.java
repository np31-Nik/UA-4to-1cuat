package madstodolist.service;

import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
@Service
public class ComentarioService {


    Logger logger = LoggerFactory.getLogger(TareaService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ComentarioRepository comentarioRepository;

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private TareaService tareaService;


    @Transactional
    public Comentario nuevoComentario(Long idUsuario, Long idTarea, String texto, Date fecha) {
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        Tarea tarea = tareaRepository.findById(idTarea).orElse(null);

        if (usuario == null || tarea == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear comentario " );
        }
        Comentario comentario = new Comentario(usuario, tarea, texto, fecha);

        comentarioRepository.save(comentario);
        return comentario;
    }

    @Transactional(readOnly = true)
    public List<Comentario> allComentarios(Long idTarea) {
        Tarea tarea = tareaService.findById(idTarea);
        List<Comentario> comentarios = new ArrayList(tarea.getComentarios());
        return comentarios;
    }

    @Transactional(readOnly = true)
    public Comentario findById(Long comentarioId) {
        logger.debug("Buscando comentario " + comentarioId);
        return comentarioRepository.findById(comentarioId).orElse(null);
    }

    @Transactional
    public void borrarComentario(Long comentarioId) {
        Comentario comentario = comentarioRepository.findById(comentarioId).orElse(null);
        if (comentario == null) {
            throw new TareaServiceException("No existe comentario con id " + comentarioId);
        }
        comentarioRepository.delete(comentario);
    }
}
