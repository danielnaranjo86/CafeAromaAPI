package com.cafearoma.api.service;

import com.cafearoma.api.dto.AuthResponse;
import com.cafearoma.api.dto.LoginRequest;
import com.cafearoma.api.dto.RegistroRequest;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.Rol;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.RolRepository;
import com.cafearoma.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registrarUsuarioCorrectamente() {

        RegistroRequest request = new RegistroRequest();
        request.setNombre("Usuario Prueba");
        request.setCorreo("usuario@cafearoma.com");
        request.setPassword("123456");
        request.setTelefono("3001234567");

        Rol rolCliente = new Rol(2L, "CLIENTE");

        when(usuarioRepository.existsByCorreo(
                "usuario@cafearoma.com"
        )).thenReturn(false);

        when(rolRepository.findByNombre("CLIENTE"))
                .thenReturn(Optional.of(rolCliente));

        when(passwordEncoder.encode("123456"))
                .thenReturn("passwordHash");

        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> {
                    Usuario usuario = invocation.getArgument(0);
                    usuario.setIdUsuario(5L);
                    return usuario;
                });

        when(jwtService.generarToken(any(Usuario.class)))
                .thenReturn("token-prueba");

        AuthResponse response =
                authService.registrarUsuario(request);

        assertNotNull(response);

        assertEquals(
                "Usuario registrado correctamente.",
                response.getMensaje()
        );

        assertEquals(
                "token-prueba",
                response.getToken()
        );

        assertNotNull(response.getUsuario());

        assertEquals(
                5L,
                response.getUsuario().getIdUsuario()
        );

        assertEquals(
                "Usuario Prueba",
                response.getUsuario().getNombre()
        );

        assertEquals(
                "usuario@cafearoma.com",
                response.getUsuario().getCorreo()
        );

        assertEquals(
                "CLIENTE",
                response.getUsuario().getRol()
        );

        verify(passwordEncoder)
                .encode("123456");

        verify(usuarioRepository)
                .save(any(Usuario.class));

        verify(clienteRepository)
                .save(any(Cliente.class));

        verify(jwtService)
                .generarToken(any(Usuario.class));
    }

    @Test
    void noDebeRegistrarCorreoDuplicado() {

        RegistroRequest request = new RegistroRequest();
        request.setNombre("Usuario Prueba");
        request.setCorreo("usuario@cafearoma.com");
        request.setPassword("123456");
        request.setTelefono("3001234567");

        when(usuarioRepository.existsByCorreo(
                "usuario@cafearoma.com"
        )).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> authService.registrarUsuario(request)
                );

        assertEquals(
                "El correo ya se encuentra registrado.",
                exception.getMessage()
        );

        verify(usuarioRepository, never())
                .save(any(Usuario.class));

        verify(clienteRepository, never())
                .save(any(Cliente.class));

        verify(jwtService, never())
                .generarToken(any(Usuario.class));
    }

    @Test
    void iniciarSesionCorrectamente() {

        LoginRequest request = new LoginRequest();
        request.setCorreo("usuario@cafearoma.com");
        request.setPassword("123456");

        Rol rolCliente = new Rol(2L, "CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(5L);
        usuario.setNombre("Usuario Prueba");
        usuario.setCorreo("usuario@cafearoma.com");
        usuario.setPasswordHash("passwordHash");
        usuario.setEstado(true);
        usuario.setRol(rolCliente);

        when(usuarioRepository.findByCorreo(
                "usuario@cafearoma.com"
        )).thenReturn(Optional.of(usuario));

        when(jwtService.generarToken(usuario))
                .thenReturn("token-login");

        AuthResponse response =
                authService.iniciarSesion(request);

        assertNotNull(response);

        assertEquals(
                "Autenticación satisfactoria.",
                response.getMensaje()
        );

        assertEquals(
                "token-login",
                response.getToken()
        );

        assertNotNull(response.getUsuario());

        assertEquals(
                "usuario@cafearoma.com",
                response.getUsuario().getCorreo()
        );

        assertEquals(
                "CLIENTE",
                response.getUsuario().getRol()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(usuarioRepository)
                .findByCorreo("usuario@cafearoma.com");

        verify(jwtService)
                .generarToken(usuario);
    }
}