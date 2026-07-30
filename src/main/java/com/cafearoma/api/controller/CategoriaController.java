package com.cafearoma.api.controller;

import com.cafearoma.api.dto.CategoriaRequest;
import com.cafearoma.api.dto.CategoriaResponse;
import com.cafearoma.api.service.CategoriaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/api/categorias")
    public ResponseEntity<List<CategoriaResponse>> listarCategorias() {
        return ResponseEntity.ok(categoriaService.listarActivas());
    }

    @PostMapping("/api/admin/categorias")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaRequest request) {
        try {
            CategoriaResponse response = categoriaService.crear(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/api/admin/categorias/{idCategoria}")
    public ResponseEntity<?> actualizarCategoria(
            @PathVariable Long idCategoria,
            @RequestBody CategoriaRequest request
    ) {
        try {
            CategoriaResponse response = categoriaService.actualizar(idCategoria, request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/api/admin/categorias/{idCategoria}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Long idCategoria) {
        try {
            categoriaService.desactivar(idCategoria);
            return ResponseEntity.ok(Map.of("mensaje", "Categoría desactivada correctamente."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }
}