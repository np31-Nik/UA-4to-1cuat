package madstodolist.service;

import madstodolist.model.Usuario;
import madstodolist.model.UsuarioRepository;
import madstodolist.model.VerificationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.saml2.Saml2RelyingPartyProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    Logger logger = LoggerFactory.getLogger(UsuarioService.class);


    public enum LoginStatus {LOGIN_OK, USER_NOT_FOUND, ERROR_PASSWORD}

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private VerificationTokenService verificationTokenService;

    @Transactional(readOnly = true)
    public LoginStatus login(String eMail, String password) {
        Optional<Usuario> usuario = usuarioRepository.findByEmail(eMail);
        if (!usuario.isPresent()) {
            return LoginStatus.USER_NOT_FOUND;
        } else if (!usuario.get().getPassword().equals(password)) {
            return LoginStatus.ERROR_PASSWORD;
        } else {
            return LoginStatus.LOGIN_OK;
        }
    }

    //Token de verificación de usuario para el registro por email
    public void createVerificationToken(Usuario user, String token) {
        VerificationToken newToken = new VerificationToken(token, user);
        verificationTokenService.saveToken(newToken);

    }


    // Se añade un usuario en la aplicación.
    // El email y password del usuario deben ser distinto de null
    // El email no debe estar registrado en la base de datos
    @Transactional
    public Usuario registrar(Usuario usuario) {
        Optional<Usuario> usuarioBD = usuarioRepository.findByEmail(usuario.getEmail());
        if (usuarioBD.isPresent())
            throw new UsuarioServiceException("El usuario " + usuario.getEmail() + " ya está registrado");
        else if (usuario.getEmail() == null)
            throw new UsuarioServiceException("El usuario no tiene email");
        else if (usuario.getPassword() == null)
            throw new UsuarioServiceException("El usuario no tiene password");
        else return usuarioRepository.save(usuario);
    }

    @Transactional(readOnly = true)
    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email).orElse(null);
    }

    @Transactional(readOnly = true)
    public Usuario findById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Usuario> listadoUsuarios(){
        List<Usuario> usuarios =  new ArrayList();
        usuarioRepository.findAll().forEach(user -> usuarios.add(user));
        return usuarios;
    }
    @Transactional(readOnly = true)
    public boolean existeAdmin(){
        List<Usuario> usuarios = listadoUsuarios();
        for (Usuario usuario : usuarios){
            if (usuario.getAdmin() == true){
                return true;
            }
        }
        return false;
    }

    //este método se usa para cambiar el bloqueo de un usuario, hay que cambiar nombre
    @Transactional
    public boolean bloquearUsuario(Long idUsuario){
        Usuario aBloquear = findById(idUsuario);

        aBloquear.setBloqueado(!aBloquear.getBloqueado());
        usuarioRepository.save(aBloquear);

        return aBloquear.getBloqueado();
    }

    @Transactional
    public boolean modificarUsuario(Long idUsuario, String nombre, Date fechaNacimiento){

        Usuario usuario = findById(idUsuario);

        usuario.setNombre(nombre);
        usuario.setFechaNacimiento(fechaNacimiento);
        usuarioRepository.save(usuario);

        return true;
    }


    public boolean modificarContrasenya(Long idUsuario, String newPassword){

        Usuario usuario = findById(idUsuario);

        usuario.setPassword(newPassword);
        usuarioRepository.save(usuario);

        return true;

    }

}
