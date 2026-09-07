package com.cafearoma.api.service;

import com.cafearoma.api.dto.DireccionEnvioRequest;
import com.cafearoma.api.dto.DireccionEnvioResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.DireccionEnvio;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.DireccionEnvioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DireccionEnvioServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private DireccionEnvioRepository direccionEnvioRepository;

    @InjectMocks
    private DireccionEnvioService direccionEnvioService;

    @Test
    void crearPrimeraDireccionCorrectamente() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        DireccionEnvioRequest request = new DireccionEnvioRequest();
        request.setDireccion("Calle 123 # 45-67");
        request.setCiudad("Bogotá");
        request.setDepartamento("Cundinamarca");
        request.setPais("Colombia");
        request.setCodigoPostal("110111");
        request.setReferencia("Apto 302");
        request.setPredeterminada(false);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(direccionEnvioRepository
                .countByClienteIdClienteAndActivaTrue(1L))
                .thenReturn(0L);

        when(direccionEnvioRepository.save(any(DireccionEnvio.class)))
                .thenAnswer(invocation -> {
                    DireccionEnvio direccion = invocation.getArgument(0);
                    direccion.setIdDireccion(1L);
                    return direccion;
                });

        DireccionEnvioResponse response =
                direccionEnvioService.crearDireccion(usuario, request);

        assertNotNull(response);
        assertEquals(1L, response.getIdDireccion());
        assertEquals("Calle 123 # 45-67", response.getDireccion());
        assertEquals("Bogotá", response.getCiudad());
        assertEquals("Colombia", response.getPais());
        assertEquals("Apto 302", response.getReferencia());

        assertTrue(response.getPredeterminada());
        assertTrue(response.getActiva());

        verify(direccionEnvioRepository)
                .quitarPredeterminada(1L);

        verify(direccionEnvioRepository)
                .save(any(DireccionEnvio.class));
    }

    @Test
    void noDebeCrearDireccionSinDireccion() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        DireccionEnvioRequest request = new DireccionEnvioRequest();
        request.setDireccion("");
        request.setCiudad("Bogotá");

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> direccionEnvioService.crearDireccion(usuario, request)
        );

        assertEquals(
                "La dirección es obligatoria.",
                exception.getMessage()
        );

        verify(direccionEnvioRepository, never())
                .save(any(DireccionEnvio.class));
    }

    @Test
    void noDebeCrearDireccionSinCiudad() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        DireccionEnvioRequest request = new DireccionEnvioRequest();
        request.setDireccion("Calle 123 # 45-67");
        request.setCiudad("");

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> direccionEnvioService.crearDireccion(usuario, request)
        );

        assertEquals(
                "La ciudad es obligatoria.",
                exception.getMessage()
        );

        verify(direccionEnvioRepository, never())
                .save(any(DireccionEnvio.class));
    }
}