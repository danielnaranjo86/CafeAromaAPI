package com.cafearoma.api.service;

import com.cafearoma.api.dto.CategoriaRequest;
import com.cafearoma.api.dto.CategoriaResponse;
import com.cafearoma.api.model.Categoria;
import com.cafearoma.api.repository.CategoriaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void crearCategoriaCorrectamente() {

        CategoriaRequest request = new CategoriaRequest();
        request.setNombreCategoria("Café en grano");
        request.setDescripcion("Café tostado en presentación de grano");

        when(categoriaRepository.existsByNombreCategoriaIgnoreCase("Café en grano"))
                .thenReturn(false);

        when(categoriaRepository.save(any(Categoria.class)))
                .thenAnswer(invocation -> {
                    Categoria categoria = invocation.getArgument(0);
                    categoria.setIdCategoria(1L);
                    return categoria;
                });

        CategoriaResponse response = categoriaService.crear(request);

        assertNotNull(response);
        assertEquals(1L, response.getIdCategoria());
        assertEquals("Café en grano", response.getNombreCategoria());
        assertEquals(
                "Café tostado en presentación de grano",
                response.getDescripcion()
        );
        assertTrue(response.getActiva());

        verify(categoriaRepository)
                .existsByNombreCategoriaIgnoreCase("Café en grano");

        verify(categoriaRepository)
                .save(any(Categoria.class));
    }

    @Test
    void noDebeCrearCategoriaDuplicada() {

        CategoriaRequest request = new CategoriaRequest();
        request.setNombreCategoria("Café en grano");
        request.setDescripcion("Descripción de prueba");

        when(categoriaRepository.existsByNombreCategoriaIgnoreCase("Café en grano"))
                .thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> categoriaService.crear(request)
        );

        assertEquals(
                "Ya existe una categoría con ese nombre.",
                exception.getMessage()
        );

        verify(categoriaRepository, never())
                .save(any(Categoria.class));
    }
}