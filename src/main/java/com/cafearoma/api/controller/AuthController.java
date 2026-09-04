package com.cafearoma.api.controller;

import com.cafearoma.api.dto.AuthResponse;
import com.cafearoma.api.dto.LoginRequest;
import com.cafearoma.api.dto.RegistroRequest;
import com.cafearoma.api.dto.UsuarioResponse;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.service.AuthService;
import com.cafearoma.api.dto.ActualizarPerfilRequest;
import com.cafearoma.api.dto.CambiarPasswordRequest;
import com.cafearoma.api.dto.PerfilResponse;
import com.cafearoma.api.service.PerfilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final PerfilService perfilService;

    public AuthController(AuthService authService, PerfilService perfilService) {
        this.authService = authService;
        this.perfilService = perfilService;
    }

    @GetMapping("/prueba")
    public ResponseEntity<Map<String, String>> prueba() {
        return ResponseEntity.ok(
                Map.of("mensaje", "Servicio web Café Aroma funcionando correctamente.")
        );
    }

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@RequestBody RegistroRequest request) {
        try {
            AuthResponse response = authService.registrarUsuario(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.iniciarSesion(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("mensaje", "Credenciales incorrectas.")
            );
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> perfil(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            PerfilResponse response = perfilService.obtenerPerfil(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(
            Authentication authentication,
            @RequestBody ActualizarPerfilRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            PerfilResponse response = perfilService.actualizarPerfil(usuario, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", e.getMessage())
            );
        }
    }

    @PutMapping("/cambiar-password")
    public ResponseEntity<?> cambiarPassword(
            Authentication authentication,
            @RequestBody CambiarPasswordRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            perfilService.cambiarPassword(usuario, request);

            return ResponseEntity.ok(
                    Map.of("mensaje", "Contraseña actualizada correctamente.")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", e.getMessage())
            );
        }
    }
}