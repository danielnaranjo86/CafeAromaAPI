package com.cafearoma.api.repository;

import com.cafearoma.api.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    List<CarritoItem> findByCarritoIdCarrito(Long idCarrito);

    Optional<CarritoItem> findByCarritoIdCarritoAndProductoIdProducto(Long idCarrito, Long idProducto);

    void deleteByCarritoIdCarrito(Long idCarrito);
}