package com.cafearoma.api.repository;

import com.cafearoma.api.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdCategoriaAndActivoTrue(Long idCategoria);

    List<Producto> findByNombreProductoContainingIgnoreCaseAndActivoTrue(String nombreProducto);
}