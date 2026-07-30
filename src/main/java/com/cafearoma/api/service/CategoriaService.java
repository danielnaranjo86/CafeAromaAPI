package com.cafearoma.api.service;

import com.cafearoma.api.dto.CategoriaRequest;
import com.cafearoma.api.dto.CategoriaResponse;
import com.cafearoma.api.model.Categoria;
import com.cafearoma.api.repository.CategoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaResponse> listarActivas() {
        return categoriaRepository.findByActivaTrue()
                .stream()
                .map(this::mapearCategoria)
                .toList();
    }

    public CategoriaResponse crear(CategoriaRequest request) {

        if (categoriaRepository.existsByNombreCategoriaIgnoreCase(request.getNombreCategoria())) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre.");
        }

        Categoria categoria = new Categoria();
        categoria.setNombreCategoria(request.getNombreCategoria());
        categoria.setDescripcion(request.getDescripcion());
        categoria.setActiva(true);

        Categoria guardada = categoriaRepository.save(categoria);

        return mapearCategoria(guardada);
    }

    public CategoriaResponse actualizar(Long idCategoria, CategoriaRequest request) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        categoria.setNombreCategoria(request.getNombreCategoria());
        categoria.setDescripcion(request.getDescripcion());

        Categoria actualizada = categoriaRepository.save(categoria);

        return mapearCategoria(actualizada);
    }

    public void desactivar(Long idCategoria) {

        Categoria categoria = categoriaRepository.findById(idCategoria)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        categoria.setActiva(false);
        categoriaRepository.save(categoria);
    }

    private CategoriaResponse mapearCategoria(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombreCategoria(),
                categoria.getDescripcion(),
                categoria.getActiva()
        );
    }
}