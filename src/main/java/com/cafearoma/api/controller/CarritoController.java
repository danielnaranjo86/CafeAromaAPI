package com.cafearoma.api.controller;

import com.cafearoma.api.dto.AgregarCarritoRequest;
import com.cafearoma.api.dto.ActualizarCantidadRequest;
import com.cafearoma.api.dto.CarritoResponse;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @GetMapping
    public ResponseEntity<?> verCarrito(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            CarritoResponse response = carritoService.verCarrito(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PostMapping("/items")
    public ResponseEntity<?> agregarProducto(
            Authentication authentication,
            @RequestBody AgregarCarritoRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            CarritoResponse response = carritoService.agregarProducto(usuario, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/items/{idItem}")
    public ResponseEntity<?> actualizarCantidad(
            Authentication authentication,
            @PathVariable Long idItem,
            @RequestBody ActualizarCantidadRequest request
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            CarritoResponse response = carritoService.actualizarCantidad(usuario, idItem, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<?> eliminarItem(
            Authentication authentication,
            @PathVariable Long idItem
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            CarritoResponse response = carritoService.eliminarItem(usuario, idItem);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping
    public ResponseEntity<?> vaciarCarrito(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            CarritoResponse response = carritoService.vaciarCarrito(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}