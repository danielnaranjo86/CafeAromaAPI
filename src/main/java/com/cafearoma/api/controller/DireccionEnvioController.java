package com.cafearoma.api.controller;

import com.cafearoma.api.dto.DireccionEnvioRequest;
import com.cafearoma.api.dto.DireccionEnvioResponse;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.service.DireccionEnvioService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/direcciones")
public class DireccionEnvioController {

    private final DireccionEnvioService direccionEnvioService;

    public DireccionEnvioController(DireccionEnvioService direccionEnvioService) {
        this.direccionEnvioService = direccionEnvioService;
    }

    @GetMapping
    public ResponseEntity<?> listarMisDirecciones(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            List<DireccionEnvioResponse> response = direccionEnvioService.listarMisDirecciones(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> crearDireccion(
            Authentication authentication,
            @RequestBody DireccionEnvioRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            DireccionEnvioResponse response = direccionEnvioService.crearDireccion(usuario, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{idDireccion}")
    public ResponseEntity<?> actualizarDireccion(
            Authentication authentication,
            @PathVariable Long idDireccion,
            @RequestBody DireccionEnvioRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            DireccionEnvioResponse response = direccionEnvioService.actualizarDireccion(usuario, idDireccion, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{idDireccion}")
    public ResponseEntity<?> desactivarDireccion(
            Authentication authentication,
            @PathVariable Long idDireccion
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            direccionEnvioService.desactivarDireccion(usuario, idDireccion);
            return ResponseEntity.ok(Map.of("mensaje", "Dirección desactivada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{idDireccion}/predeterminada")
    public ResponseEntity<?> marcarComoPredeterminada(
            Authentication authentication,
            @PathVariable Long idDireccion
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            DireccionEnvioResponse response = direccionEnvioService.marcarComoPredeterminada(usuario, idDireccion);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}