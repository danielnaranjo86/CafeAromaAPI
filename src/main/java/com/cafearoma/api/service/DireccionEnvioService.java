package com.cafearoma.api.service;

import com.cafearoma.api.dto.DireccionEnvioRequest;
import com.cafearoma.api.dto.DireccionEnvioResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.DireccionEnvio;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.DireccionEnvioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DireccionEnvioService {

    private final ClienteRepository clienteRepository;
    private final DireccionEnvioRepository direccionEnvioRepository;

    public DireccionEnvioService(
            ClienteRepository clienteRepository,
            DireccionEnvioRepository direccionEnvioRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.direccionEnvioRepository = direccionEnvioRepository;
    }

    public List<DireccionEnvioResponse> listarMisDirecciones(Usuario usuario) {
        Cliente cliente = obtenerCliente(usuario);

        return direccionEnvioRepository.findByClienteIdClienteAndActivaTrue(cliente.getIdCliente())
                .stream()
                .map(this::mapearDireccion)
                .toList();
    }

    @Transactional
    public DireccionEnvioResponse crearDireccion(Usuario usuario, DireccionEnvioRequest request) {
        Cliente cliente = obtenerCliente(usuario);
        validarDireccion(request);

        boolean esPrimeraDireccion = direccionEnvioRepository.countByClienteIdClienteAndActivaTrue(cliente.getIdCliente()) == 0;
        boolean seraPredeterminada = esPrimeraDireccion || Boolean.TRUE.equals(request.getPredeterminada());

        if (seraPredeterminada) {
            direccionEnvioRepository.quitarPredeterminada(cliente.getIdCliente());
        }

        DireccionEnvio direccion = new DireccionEnvio();
        direccion.setCliente(cliente);
        direccion.setDireccion(request.getDireccion());
        direccion.setCiudad(request.getCiudad());
        direccion.setDepartamento(request.getDepartamento());
        direccion.setPais(request.getPais() == null || request.getPais().isBlank() ? "Colombia" : request.getPais());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setReferencia(request.getReferencia());
        direccion.setPredeterminada(seraPredeterminada);
        direccion.setActiva(true);

        DireccionEnvio guardada = direccionEnvioRepository.save(direccion);

        return mapearDireccion(guardada);
    }

    @Transactional
    public DireccionEnvioResponse actualizarDireccion(
            Usuario usuario,
            Long idDireccion,
            DireccionEnvioRequest request
    ) {
        Cliente cliente = obtenerCliente(usuario);
        validarDireccion(request);

        DireccionEnvio direccion = direccionEnvioRepository
                .findByIdDireccionAndClienteIdClienteAndActivaTrue(idDireccion, cliente.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada."));

        if (Boolean.TRUE.equals(request.getPredeterminada())) {
            direccionEnvioRepository.quitarPredeterminada(cliente.getIdCliente());
            direccion.setPredeterminada(true);
        }

        direccion.setDireccion(request.getDireccion());
        direccion.setCiudad(request.getCiudad());
        direccion.setDepartamento(request.getDepartamento());
        direccion.setPais(request.getPais() == null || request.getPais().isBlank() ? "Colombia" : request.getPais());
        direccion.setCodigoPostal(request.getCodigoPostal());
        direccion.setReferencia(request.getReferencia());

        DireccionEnvio actualizada = direccionEnvioRepository.save(direccion);

        return mapearDireccion(actualizada);
    }

    @Transactional
    public void desactivarDireccion(Usuario usuario, Long idDireccion) {
        Cliente cliente = obtenerCliente(usuario);

        DireccionEnvio direccion = direccionEnvioRepository
                .findByIdDireccionAndClienteIdClienteAndActivaTrue(idDireccion, cliente.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada."));

        direccion.setActiva(false);
        direccion.setPredeterminada(false);

        direccionEnvioRepository.save(direccion);
    }

    @Transactional
    public DireccionEnvioResponse marcarComoPredeterminada(Usuario usuario, Long idDireccion) {
        Cliente cliente = obtenerCliente(usuario);

        DireccionEnvio direccion = direccionEnvioRepository
                .findByIdDireccionAndClienteIdClienteAndActivaTrue(idDireccion, cliente.getIdCliente())
                .orElseThrow(() -> new IllegalArgumentException("Dirección no encontrada."));

        direccionEnvioRepository.quitarPredeterminada(cliente.getIdCliente());

        direccion.setPredeterminada(true);

        DireccionEnvio actualizada = direccionEnvioRepository.save(direccion);

        return mapearDireccion(actualizada);
    }

    private Cliente obtenerCliente(Usuario usuario) {
        return clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado no tiene perfil de cliente."));
    }

    private void validarDireccion(DireccionEnvioRequest request) {
        if (request.getDireccion() == null || request.getDireccion().isBlank()) {
            throw new IllegalArgumentException("La dirección es obligatoria.");
        }

        if (request.getCiudad() == null || request.getCiudad().isBlank()) {
            throw new IllegalArgumentException("La ciudad es obligatoria.");
        }
    }

    private DireccionEnvioResponse mapearDireccion(DireccionEnvio direccion) {
        return new DireccionEnvioResponse(
                direccion.getIdDireccion(),
                direccion.getDireccion(),
                direccion.getCiudad(),
                direccion.getDepartamento(),
                direccion.getPais(),
                direccion.getCodigoPostal(),
                direccion.getReferencia(),
                direccion.getPredeterminada(),
                direccion.getActiva()
        );
    }
}