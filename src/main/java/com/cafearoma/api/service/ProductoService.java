package com.cafearoma.api.service;

import com.cafearoma.api.dto.CategoriaResponse;
import com.cafearoma.api.dto.ProductoRequest;
import com.cafearoma.api.dto.ProductoResponse;
import com.cafearoma.api.model.Categoria;
import com.cafearoma.api.model.Producto;
import com.cafearoma.api.repository.CategoriaRepository;
import com.cafearoma.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    public List<ProductoResponse> listarActivos() {
        return productoRepository.findByActivoTrue()
                .stream()
                .map(this::mapearProducto)
                .toList();
    }

    public ProductoResponse buscarPorId(Long idProducto) {
        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("Producto no disponible.");
        }

        return mapearProducto(producto);
    }

    public List<ProductoResponse> listarPorCategoria(Long idCategoria) {
        return productoRepository.findByCategoriaIdCategoriaAndActivoTrue(idCategoria)
                .stream()
                .map(this::mapearProducto)
                .toList();
    }

    public List<ProductoResponse> buscarPorNombre(String nombre) {
        return productoRepository.findByNombreProductoContainingIgnoreCaseAndActivoTrue(nombre)
                .stream()
                .map(this::mapearProducto)
                .toList();
    }

    public ProductoResponse crear(ProductoRequest request) {

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        Producto producto = new Producto();
        producto.setNombreProducto(request.getNombreProducto());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setActivo(true);
        producto.setCategoria(categoria);

        Producto guardado = productoRepository.save(producto);

        return mapearProducto(guardado);
    }

    public ProductoResponse actualizar(Long idProducto, ProductoRequest request) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        Categoria categoria = categoriaRepository.findById(request.getIdCategoria())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada."));

        producto.setNombreProducto(request.getNombreProducto());
        producto.setDescripcion(request.getDescripcion());
        producto.setPrecio(request.getPrecio());
        producto.setStock(request.getStock());
        producto.setImagenUrl(request.getImagenUrl());
        producto.setCategoria(categoria);

        Producto actualizado = productoRepository.save(producto);

        return mapearProducto(actualizado);
    }

    public void desactivar(Long idProducto) {

        Producto producto = productoRepository.findById(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        producto.setActivo(false);
        productoRepository.save(producto);
    }

    private ProductoResponse mapearProducto(Producto producto) {

        Categoria categoria = producto.getCategoria();

        CategoriaResponse categoriaResponse = new CategoriaResponse(
                categoria.getIdCategoria(),
                categoria.getNombreCategoria(),
                categoria.getDescripcion(),
                categoria.getActiva()
        );

        return new ProductoResponse(
                producto.getIdProducto(),
                producto.getNombreProducto(),
                producto.getDescripcion(),
                producto.getPrecio(),
                producto.getStock(),
                producto.getImagenUrl(),
                producto.getActivo(),
                categoriaResponse
        );
    }
}