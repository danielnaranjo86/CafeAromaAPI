package com.cafearoma.api.repository;

import com.cafearoma.api.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    Optional<Carrito> findByClienteIdClienteAndEstado(Long idCliente, String estado);
}