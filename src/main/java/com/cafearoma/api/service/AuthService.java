package com.cafearoma.api.service;

import com.cafearoma.api.dto.LoginRequest;
import com.cafearoma.api.dto.RegistroRequest;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;

    public AuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Servicio para registrar un nuevo usuario
    public String registrarUsuario(RegistroRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            return "Error: el correo ya se encuentra registrado.";
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPassword(request.getPassword());

        usuarioRepository.save(usuario);

        return "Usuario registrado correctamente.";
    }

    // Servicio para iniciar sesión validando correo y contraseña
    public String iniciarSesion(LoginRequest request) {

        Optional<Usuario> usuarioEncontrado = usuarioRepository.findByCorreo(request.getCorreo());

        if (usuarioEncontrado.isEmpty()) {
            return "Error en la autenticación: usuario no encontrado.";
        }

        Usuario usuario = usuarioEncontrado.get();

        if (usuario.getPassword().equals(request.getPassword())) {
            return "Autenticación satisfactoria. Bienvenido, " + usuario.getNombre() + ".";
        } else {
            return "Error en la autenticación: contraseña incorrecta.";
        }
    }
}