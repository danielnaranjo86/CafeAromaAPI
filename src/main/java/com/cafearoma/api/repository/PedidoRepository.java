package com.cafearoma.api.repository;

import com.cafearoma.api.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByClienteIdClienteOrderByFechaPedidoDesc(Long idCliente);

    List<Pedido> findAllByOrderByFechaPedidoDesc();
}