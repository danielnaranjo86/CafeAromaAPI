package com.cafearoma.api.controller;

import com.cafearoma.api.dto.PagoRequest;
import com.cafearoma.api.dto.PagoResponse;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.service.PagoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
public class PagoController {

    private final PagoService pagoService;

    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/simular")
    public ResponseEntity<?> simularPago(
            Authentication authentication,
            @RequestBody PagoRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            PagoResponse response = pagoService.simularPago(usuario, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}