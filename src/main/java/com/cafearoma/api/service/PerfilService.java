package com.cafearoma.api.service;

import com.cafearoma.api.dto.ActualizarPerfilRequest;
import com.cafearoma.api.dto.CambiarPasswordRequest;
import com.cafearoma.api.dto.PerfilResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PerfilService {

    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    public PerfilService(
            UsuarioRepository usuarioRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public PerfilResponse obtenerPerfil(Usuario usuarioAutenticado) {
        Usuario usuario = obtenerUsuarioActualizado(usuarioAutenticado);

        Optional<Cliente> clienteOptional =
                clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario());

        String telefono = clienteOptional
                .map(Cliente::getTelefono)
                .orElse(null);

        return mapearPerfil(usuario, telefono);
    }

    public PerfilResponse actualizarPerfil(
            Usuario usuarioAutenticado,
            ActualizarPerfilRequest request
    ) {
        Usuario usuario = obtenerUsuarioActualizado(usuarioAutenticado);

        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        usuario.setNombre(request.getNombre());
        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Optional<Cliente> clienteOptional =
                clienteRepository.findByUsuarioIdUsuario(usuarioGuardado.getIdUsuario());

        String telefono = null;

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            cliente.setTelefono(request.getTelefono());
            Cliente clienteGuardado = clienteRepository.save(cliente);
            telefono = clienteGuardado.getTelefono();
        }

        return mapearPerfil(usuarioGuardado, telefono);
    }

    public void cambiarPassword(
            Usuario usuarioAutenticado,
            CambiarPasswordRequest request
    ) {
        Usuario usuario = obtenerUsuarioActualizado(usuarioAutenticado);

        if (request.getPasswordActual() == null || request.getPasswordActual().isBlank()) {
            throw new IllegalArgumentException("La contraseña actual es obligatoria.");
        }

        if (request.getPasswordNueva() == null || request.getPasswordNueva().length() < 6) {
            throw new IllegalArgumentException("La nueva contraseña debe tener mínimo 6 caracteres.");
        }

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new IllegalArgumentException("La contraseña actual no es correcta.");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }

    private Usuario obtenerUsuarioActualizado(Usuario usuarioAutenticado) {
        return usuarioRepository.findById(usuarioAutenticado.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));
    }

    private PerfilResponse mapearPerfil(Usuario usuario, String telefono) {
        return new PerfilResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre(),
                telefono
        );
    }
}