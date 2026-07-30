package com.cafearoma.api.controller;

import com.cafearoma.api.dto.ProductoRequest;
import com.cafearoma.api.dto.ProductoResponse;
import com.cafearoma.api.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/api/productos")
    public ResponseEntity<List<ProductoResponse>> listarProductos() {
        return ResponseEntity.ok(productoService.listarActivos());
    }

    @GetMapping("/api/productos/{idProducto}")
    public ResponseEntity<?> buscarProductoPorId(@PathVariable Long idProducto) {
        try {
            return ResponseEntity.ok(productoService.buscarPorId(idProducto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @GetMapping("/api/productos/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoResponse>> listarPorCategoria(@PathVariable Long idCategoria) {
        return ResponseEntity.ok(productoService.listarPorCategoria(idCategoria));
    }

    @GetMapping("/api/productos/buscar")
    public ResponseEntity<List<ProductoResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.buscarPorNombre(nombre));
    }

    @PostMapping("/api/admin/productos")
    public ResponseEntity<?> crearProducto(@RequestBody ProductoRequest request) {
        try {
            ProductoResponse response = productoService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/api/admin/productos/{idProducto}")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long idProducto,
            @RequestBody ProductoRequest request
    ) {
        try {
            ProductoResponse response = productoService.actualizar(idProducto, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/api/admin/productos/{idProducto}")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long idProducto) {
        try {
            productoService.desactivar(idProducto);
            return ResponseEntity.ok(Map.of("mensaje", "Producto desactivado correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}