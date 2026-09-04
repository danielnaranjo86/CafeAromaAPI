package com.cafearoma.api.controller;

import com.cafearoma.api.dto.EstadoPedidoRequest;
import com.cafearoma.api.dto.PedidoResponse;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.service.PedidoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping("/api/pedidos")
    public ResponseEntity<?> crearPedido(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            PedidoResponse response = pedidoService.crearPedidoDesdeCarrito(usuario);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/api/pedidos/mis-pedidos")
    public ResponseEntity<?> listarMisPedidos(Authentication authentication) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            List<PedidoResponse> response = pedidoService.listarMisPedidos(usuario);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/api/pedidos/{idPedido}")
    public ResponseEntity<?> buscarMiPedido(
            Authentication authentication,
            @PathVariable Long idPedido
    ) {
        try {
            Usuario usuario = (Usuario) authentication.getPrincipal();
            PedidoResponse response = pedidoService.buscarMiPedidoPorId(usuario, idPedido);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/api/admin/pedidos")
    public ResponseEntity<List<PedidoResponse>> listarTodosLosPedidos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @PutMapping("/api/admin/pedidos/{idPedido}/estado")
    public ResponseEntity<?> cambiarEstadoPedido(
            @PathVariable Long idPedido,
            @RequestBody EstadoPedidoRequest request
    ) {
        try {
            PedidoResponse response = pedidoService.cambiarEstado(idPedido, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}