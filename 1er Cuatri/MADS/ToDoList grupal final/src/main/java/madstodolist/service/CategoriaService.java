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

@Service
public class CategoriaService {

    Logger logger = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    @Transactional
    public Categoria nuevaCategoriaUsuario(Long idUsuario, String nombreCat, String colorCat) {
        logger.debug("Añadiendo Categoria " + nombreCat + " al usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al crear Categoria " + nombreCat);
        }
        Categoria c = new Categoria(usuario, nombreCat,colorCat);
        categoriaRepository.save(c);
        return c;
    }

    @Transactional(readOnly = true)
    public List<Categoria> allCatUsuario(Long idUsuario) {
        logger.debug("Devolviendo todas las Categorias del usuario " + idUsuario);
        Usuario usuario = usuarioRepository.findById(idUsuario).orElse(null);
        if (usuario == null) {
            throw new TareaServiceException("Usuario " + idUsuario + " no existe al listar Categorias ");
        }
        List<Categoria> cats = new ArrayList(usuario.getCategorias());
        Collections.sort(cats, (a, b) -> a.getId() < b.getId() ? -1 : a.getId() == b.getId() ? 0 : 1);
        return cats;
    }

    @Transactional(readOnly = true)
    public Categoria findById(Long catId) {
        logger.debug("Buscando categoria " + catId);
        return categoriaRepository.findById(catId).orElse(null);
    }

    @Transactional
    public Categoria modificaCategoria(Long idCat, String nuevoNombre, String nuevoColor) {
        logger.debug("Modificando categoria " + idCat + " - " + nuevoNombre);
        Categoria cat = categoriaRepository.findById(idCat).orElse(null);
        if (cat == null) {
            throw new TareaServiceException("No existe categoria con id " + idCat);
        }
        cat.setNombre(nuevoNombre);
        cat.setColor(nuevoColor);
        categoriaRepository.save(cat);
        return cat;
    }

    @Transactional
    public void borraCategoria(Long idCat) {
        logger.debug("Borrando Categoria " + idCat);
        Categoria cat = categoriaRepository.findById(idCat).orElse(null);
        if (cat == null) {
            throw new TareaServiceException("No existe categoria con id " + idCat);
        }
        categoriaRepository.delete(cat);
    }
}
