package com.cafearoma.api.repository;

import com.cafearoma.api.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByPedidoIdPedido(Long idPedido);

    boolean existsByPedidoIdPedidoAndEstadoPago(Long idPedido, String estadoPago);
}