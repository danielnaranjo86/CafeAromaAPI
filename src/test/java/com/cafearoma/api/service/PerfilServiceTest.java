package com.cafearoma.api.service;

import com.cafearoma.api.dto.ActualizarPerfilRequest;
import com.cafearoma.api.dto.CambiarPasswordRequest;
import com.cafearoma.api.dto.PerfilResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.Rol;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerfilServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PerfilService perfilService;

    @Test
    void obtenerPerfilCorrectamente() {

        Rol rol = new Rol();
        rol.setIdRol(2L);
        rol.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setNombre("Daniel Naranjo");
        usuario.setCorreo("daniel@cafearoma.com");
        usuario.setRol(rol);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);
        cliente.setTelefono("3001234567");

        when(usuarioRepository.findById(3L))
                .thenReturn(Optional.of(usuario));

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        PerfilResponse response =
                perfilService.obtenerPerfil(usuario);

        assertNotNull(response);
        assertEquals(3L, response.getIdUsuario());
        assertEquals("Daniel Naranjo", response.getNombre());
        assertEquals("daniel@cafearoma.com", response.getCorreo());
        assertEquals("CLIENTE", response.getRol());
        assertEquals("3001234567", response.getTelefono());

        verify(usuarioRepository).findById(3L);
        verify(clienteRepository).findByUsuarioIdUsuario(3L);
    }

    @Test
    void actualizarPerfilCorrectamente() {

        Rol rol = new Rol();
        rol.setIdRol(2L);
        rol.setNombre("CLIENTE");

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setNombre("Daniel Naranjo");
        usuario.setCorreo("daniel@cafearoma.com");
        usuario.setRol(rol);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);
        cliente.setTelefono("3001234567");

        ActualizarPerfilRequest request =
                new ActualizarPerfilRequest();

        request.setNombre("Daniel Naranjo Actualizado");
        request.setTelefono("3112223344");

        when(usuarioRepository.findById(3L))
                .thenReturn(Optional.of(usuario));

        when(usuarioRepository.save(usuario))
                .thenReturn(usuario);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(clienteRepository.save(cliente))
                .thenReturn(cliente);

        PerfilResponse response =
                perfilService.actualizarPerfil(usuario, request);

        assertNotNull(response);
        assertEquals(
                "Daniel Naranjo Actualizado",
                response.getNombre()
        );

        assertEquals(
                "3112223344",
                response.getTelefono()
        );

        verify(usuarioRepository).save(usuario);
        verify(clienteRepository).save(cliente);
    }

    @Test
    void cambiarPasswordCorrectamente() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setPasswordHash("hashAnterior");

        CambiarPasswordRequest request =
                new CambiarPasswordRequest();

        request.setPasswordActual("123456");
        request.setPasswordNueva("Nueva123");

        when(usuarioRepository.findById(3L))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(
                "123456",
                "hashAnterior"
        )).thenReturn(true);

        when(passwordEncoder.encode("Nueva123"))
                .thenReturn("hashNuevo");

        perfilService.cambiarPassword(usuario, request);

        assertEquals(
                "hashNuevo",
                usuario.getPasswordHash()
        );

        verify(passwordEncoder)
                .matches("123456", "hashAnterior");

        verify(passwordEncoder)
                .encode("Nueva123");

        verify(usuarioRepository)
                .save(usuario);
    }

    @Test
    void noDebeCambiarPasswordSiPasswordActualEsIncorrecta() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);
        usuario.setPasswordHash("hashAnterior");

        CambiarPasswordRequest request =
                new CambiarPasswordRequest();

        request.setPasswordActual("incorrecta");
        request.setPasswordNueva("Nueva123");

        when(usuarioRepository.findById(3L))
                .thenReturn(Optional.of(usuario));

        when(passwordEncoder.matches(
                "incorrecta",
                "hashAnterior"
        )).thenReturn(false);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> perfilService.cambiarPassword(
                                usuario,
                                request
                        )
                );

        assertEquals(
                "La contraseña actual no es correcta.",
                exception.getMessage()
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(usuarioRepository, never())
                .save(any(Usuario.class));
    }
}