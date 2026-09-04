package com.cafearoma.api.service;

import com.cafearoma.api.dto.PagoRequest;
import com.cafearoma.api.dto.PagoResponse;
import com.cafearoma.api.model.*;
import com.cafearoma.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagoService {

    private final ClienteRepository clienteRepository;
    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PagoService(
            ClienteRepository clienteRepository,
            PedidoRepository pedidoRepository,
            PagoRepository pagoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.pedidoRepository = pedidoRepository;
        this.pagoRepository = pagoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public PagoResponse simularPago(Usuario usuario, PagoRequest request) {

        Cliente cliente = clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado no tiene perfil de cliente."));

        Pedido pedido = pedidoRepository.findById(request.getIdPedido())
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (!pedido.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario autenticado.");
        }

        if (!"PENDIENTE".equals(pedido.getEstado())) {
            throw new IllegalArgumentException("Solo se pueden pagar pedidos en estado PENDIENTE.");
        }

        if (pagoRepository.existsByPedidoIdPedidoAndEstadoPago(pedido.getIdPedido(), "APROBADO")) {
            throw new IllegalArgumentException("Este pedido ya tiene un pago aprobado.");
        }

        boolean aprobado = Boolean.TRUE.equals(request.getAprobado());

        Pago pago = new Pago();
        pago.setPedido(pedido);
        pago.setMetodoPago(request.getMetodoPago());
        pago.setFechaPago(LocalDateTime.now());
        pago.setValorPagado(pedido.getTotal());

        if (aprobado) {
            pago.setEstadoPago("APROBADO");
            pedido.setEstado("PAGADO");
        } else {
            pago.setEstadoPago("RECHAZADO");
            pedido.setEstado("CANCELADO");
            devolverStock(pedido);
        }

        pedidoRepository.save(pedido);
        Pago pagoGuardado = pagoRepository.save(pago);

        return new PagoResponse(
                pagoGuardado.getIdPago(),
                pedido.getIdPedido(),
                pagoGuardado.getMetodoPago(),
                pagoGuardado.getValorPagado(),
                pagoGuardado.getEstadoPago(),
                pagoGuardado.getFechaPago()
        );
    }

    private void devolverStock(Pedido pedido) {
        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(pedido.getIdPedido());

        for (DetallePedido detalle : detalles) {
            Producto producto = detalle.getProducto();
            producto.setStock(producto.getStock() + detalle.getCantidad());
            productoRepository.save(producto);
        }
    }
}