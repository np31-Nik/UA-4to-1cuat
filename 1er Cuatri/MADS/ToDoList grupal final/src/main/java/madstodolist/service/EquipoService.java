package madstodolist.service;

import madstodolist.controller.exception.EquipoNotFoundException;
import madstodolist.controller.exception.UsuarioNotFoundException;
import madstodolist.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EquipoService {
    Logger logger = LoggerFactory.getLogger(EquipoService.class);

    @Autowired
    EquipoRepository equipoRepository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Transactional
    public Equipo crearEquipo(String nombre) {
        Equipo equipo = new Equipo(nombre);
        return equipoRepository.save(equipo);
    }

    @Transactional(readOnly = true)
    public Equipo recuperarEquipo(Long id) {
        return equipoRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Equipo> findAllOrderedByName(){
        List<Equipo> equipos = new ArrayList();
        equipos = equipoRepository.findAll();
        Collections.sort(equipos, (Equipo equipo1, Equipo equipo2) -> equipo1.getNombre().compareToIgnoreCase(equipo2.getNombre()) );
        return equipos;
    }

    @Transactional
    public boolean addUsuarioEquipo(Long idUsuario, Long idEquipo){
        Equipo e = equipoRepository.findById(idEquipo).orElse(null);
        if(e != null){
            Usuario u = usuarioRepository.findById(idUsuario).orElse(null);
            if(u != null){
                e.addUsuario(u);
                return true;
            }
            return false;
        }
        return false;
    }


    @Transactional
    public List<Usuario> usuariosEquipo(Long idEquipo){
        Equipo e = equipoRepository.findById(idEquipo).orElse(null);
        if (e != null){
            List<Usuario> usuarios = new ArrayList<Usuario>(e.getUsuarios());
            return usuarios;
        }
        return null;
    }

    @Transactional
    public boolean eliminarUsuario(Long idUsuario, Long idEquipo){
        Equipo e = equipoRepository.findById(idEquipo).orElse(null);
        if(e == null){
            throw new EquipoNotFoundException();
        }

        Usuario u = usuarioRepository.findById(idUsuario).orElse(null);
        if(u == null){
            throw new UsuarioNotFoundException();
        }

        e.deleteUser(u);
        u.getEquipos().remove(e);
        return true;
    }

    @Transactional
    public void eliminarEquipo(Long id) {

        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if (equipo == null) {
            throw new EquipoNotFoundException();
        }
        equipoRepository.delete(equipo);

    }


    @Transactional
    public void cambiarNombre(Long id, String nuevoNombre){

        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if(equipo == null) {
            throw new EquipoNotFoundException();
        }
        equipo.setNombre(nuevoNombre);
        equipoRepository.save(equipo);
    }

    @Transactional
    public void cambiarDesc(Long id, String nuevoDesc){
        Equipo equipo = equipoRepository.findById(id).orElse(null);
        if(equipo == null) {
            throw new EquipoNotFoundException();
        }
        equipo.setDesc(nuevoDesc);
        equipoRepository.save(equipo);
    }
}