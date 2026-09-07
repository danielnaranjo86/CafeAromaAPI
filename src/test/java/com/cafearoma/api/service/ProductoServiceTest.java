package com.cafearoma.api.service;

import com.cafearoma.api.dto.ProductoRequest;
import com.cafearoma.api.dto.ProductoResponse;
import com.cafearoma.api.model.Categoria;
import com.cafearoma.api.model.Producto;
import com.cafearoma.api.repository.CategoriaRepository;
import com.cafearoma.api.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crearProductoCorrectamente() {

        ProductoRequest request = new ProductoRequest();
        request.setNombreProducto("Café Premium");
        request.setDescripcion("Café colombiano de alta calidad");
        request.setPrecio(new BigDecimal("45000"));
        request.setStock(20);
        request.setImagenUrl("https://ejemplo.com/cafe-premium.jpg");
        request.setIdCategoria(1L);

        Categoria categoria = new Categoria();
        categoria.setIdCategoria(1L);
        categoria.setNombreCategoria("Café");
        categoria.setDescripcion("Productos de café");
        categoria.setActiva(true);

        when(categoriaRepository.findById(1L))
                .thenReturn(Optional.of(categoria));

        when(productoRepository.save(any(Producto.class)))
                .thenAnswer(invocation -> {
                    Producto producto = invocation.getArgument(0);
                    producto.setIdProducto(1L);
                    return producto;
                });

        ProductoResponse response = productoService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getIdProducto());
        assertEquals("Café Premium", response.getNombreProducto());
        assertEquals(
                "Café colombiano de alta calidad",
                response.getDescripcion()
        );
        assertEquals(new BigDecimal("45000"), response.getPrecio());
        assertEquals(20, response.getStock());
        assertTrue(response.getActivo());

        assertNotNull(response.getCategoria());
        assertEquals(1L, response.getCategoria().getIdCategoria());
        assertEquals(
                "Café",
                response.getCategoria().getNombreCategoria()
        );

        verify(categoriaRepository).findById(1L);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void noDebeCrearProductoSiCategoriaNoExiste() {

        ProductoRequest request = new ProductoRequest();
        request.setNombreProducto("Café Premium");
        request.setDescripcion("Producto de prueba");
        request.setPrecio(new BigDecimal("45000"));
        request.setStock(20);
        request.setIdCategoria(99L);

        when(categoriaRepository.findById(99L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productoService.crear(request)
        );

        assertEquals(
                "Categoría no encontrada.",
                exception.getMessage()
        );

        verify(productoRepository, never())
                .save(any(Producto.class));
    }
}