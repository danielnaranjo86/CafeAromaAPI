package com.cafearoma.api.service;

import com.cafearoma.api.dto.AuthResponse;
import com.cafearoma.api.dto.LoginRequest;
import com.cafearoma.api.dto.RegistroRequest;
import com.cafearoma.api.dto.UsuarioResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.Rol;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.RolRepository;
import com.cafearoma.api.repository.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            RolRepository rolRepository,
            ClienteRepository clienteRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public AuthResponse registrarUsuario(RegistroRequest request) {

        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya se encuentra registrado.");
        }

        Rol rolCliente = rolRepository.findByNombre("CLIENTE")
                .orElseThrow(() -> new IllegalArgumentException("El rol CLIENTE no existe."));

        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado(true);
        usuario.setRol(rolCliente);

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        Cliente cliente = new Cliente();
        cliente.setUsuario(usuarioGuardado);
        cliente.setTelefono(request.getTelefono());

        clienteRepository.save(cliente);

        String token = jwtService.generarToken(usuarioGuardado);

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuarioGuardado.getIdUsuario(),
                usuarioGuardado.getNombre(),
                usuarioGuardado.getCorreo(),
                usuarioGuardado.getRol().getNombre()
        );

        return new AuthResponse("Usuario registrado correctamente.", token, usuarioResponse);
    }

    public AuthResponse iniciarSesion(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getPassword()
                )
        );

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        String token = jwtService.generarToken(usuario);

        UsuarioResponse usuarioResponse = new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol().getNombre()
        );

        return new AuthResponse("Autenticación satisfactoria.", token, usuarioResponse);
    }
}