package com.cafearoma.api.controller;

import com.cafearoma.api.dto.LoginRequest;
import com.cafearoma.api.dto.RegistroRequest;
import com.cafearoma.api.service.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // Endpoint de prueba para verificar que la API está funcionando
    @GetMapping("/prueba")
    public String prueba() {
        return "Servicio web Café Aroma funcionando correctamente.";
    }

    // Endpoint para registrar usuarios
    @PostMapping("/registro")
    public String registrar(@RequestBody RegistroRequest request) {
        return authService.registrarUsuario(request);
    }

    // Endpoint para iniciar sesión
    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.iniciarSesion(request);
    }
}