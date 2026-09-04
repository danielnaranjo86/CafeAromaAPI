package com.cafearoma.api.service;

import com.cafearoma.api.dto.EstadoPedidoRequest;
import com.cafearoma.api.dto.PedidoItemResponse;
import com.cafearoma.api.dto.PedidoResponse;
import com.cafearoma.api.model.*;
import com.cafearoma.api.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
public class PedidoService {

    private final ClienteRepository clienteRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;
    private final PedidoRepository pedidoRepository;
    private final DetallePedidoRepository detallePedidoRepository;
    private final ProductoRepository productoRepository;

    public PedidoService(
            ClienteRepository clienteRepository,
            CarritoRepository carritoRepository,
            CarritoItemRepository carritoItemRepository,
            PedidoRepository pedidoRepository,
            DetallePedidoRepository detallePedidoRepository,
            ProductoRepository productoRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
        this.pedidoRepository = pedidoRepository;
        this.detallePedidoRepository = detallePedidoRepository;
        this.productoRepository = productoRepository;
    }

    @Transactional
    public PedidoResponse crearPedidoDesdeCarrito(Usuario usuario) {

        Cliente cliente = obtenerCliente(usuario);

        Carrito carrito = carritoRepository
                .findByClienteIdClienteAndEstado(cliente.getIdCliente(), "ACTIVO")
                .orElseThrow(() -> new IllegalArgumentException("No tienes un carrito activo."));

        List<CarritoItem> items = carritoItemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());

        if (items.isEmpty()) {
            throw new IllegalArgumentException("El carrito está vacío.");
        }

        BigDecimal total = BigDecimal.ZERO;

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new IllegalArgumentException("El producto " + producto.getNombreProducto() + " no está disponible.");
            }

            if (item.getCantidad() > producto.getStock()) {
                throw new IllegalArgumentException("No hay stock suficiente para el producto " + producto.getNombreProducto() + ".");
            }

            BigDecimal subtotal = item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()));
            total = total.add(subtotal);
        }

        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(total);

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        for (CarritoItem item : items) {
            Producto producto = item.getProducto();

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(item.getPrecioUnitario());
            detalle.setSubtotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));

            detallePedidoRepository.save(detalle);

            producto.setStock(producto.getStock() - item.getCantidad());
            productoRepository.save(producto);
        }

        carrito.setEstado("CERRADO");
        carritoRepository.save(carrito);

        return mapearPedido(pedidoGuardado);
    }

    public List<PedidoResponse> listarMisPedidos(Usuario usuario) {
        Cliente cliente = obtenerCliente(usuario);

        return pedidoRepository.findByClienteIdClienteOrderByFechaPedidoDesc(cliente.getIdCliente())
                .stream()
                .map(this::mapearPedido)
                .toList();
    }

    public PedidoResponse buscarMiPedidoPorId(Usuario usuario, Long idPedido) {
        Cliente cliente = obtenerCliente(usuario);

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        if (!pedido.getCliente().getIdCliente().equals(cliente.getIdCliente())) {
            throw new IllegalArgumentException("El pedido no pertenece al usuario autenticado.");
        }

        return mapearPedido(pedido);
    }

    public List<PedidoResponse> listarTodos() {
        return pedidoRepository.findAllByOrderByFechaPedidoDesc()
                .stream()
                .map(this::mapearPedido)
                .toList();
    }

    public PedidoResponse cambiarEstado(Long idPedido, EstadoPedidoRequest request) {

        Set<String> estadosPermitidos = Set.of(
                "PENDIENTE",
                "PAGADO",
                "EN_PREPARACION",
                "ENVIADO",
                "ENTREGADO",
                "CANCELADO"
        );

        if (request.getEstado() == null || !estadosPermitidos.contains(request.getEstado())) {
            throw new IllegalArgumentException("Estado de pedido no válido.");
        }

        Pedido pedido = pedidoRepository.findById(idPedido)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado."));

        pedido.setEstado(request.getEstado());

        Pedido actualizado = pedidoRepository.save(pedido);

        return mapearPedido(actualizado);
    }

    public PedidoResponse mapearPedido(Pedido pedido) {

        List<DetallePedido> detalles = detallePedidoRepository.findByPedidoIdPedido(pedido.getIdPedido());

        List<PedidoItemResponse> items = detalles.stream()
                .map(detalle -> new PedidoItemResponse(
                        detalle.getIdDetalle(),
                        detalle.getProducto().getIdProducto(),
                        detalle.getProducto().getNombreProducto(),
                        detalle.getCantidad(),
                        detalle.getPrecioUnitario(),
                        detalle.getSubtotal()
                ))
                .toList();

        return new PedidoResponse(
                pedido.getIdPedido(),
                pedido.getFechaPedido(),
                pedido.getEstado(),
                pedido.getTotal(),
                items
        );
    }

    private Cliente obtenerCliente(Usuario usuario) {
        return clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado no tiene perfil de cliente."));
    }
}