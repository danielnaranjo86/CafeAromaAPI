package com.cafearoma.api.service;

import com.cafearoma.api.dto.PagoRequest;
import com.cafearoma.api.dto.PagoResponse;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.DetallePedido;
import com.cafearoma.api.model.Pago;
import com.cafearoma.api.model.Pedido;
import com.cafearoma.api.model.Producto;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.DetallePedidoRepository;
import com.cafearoma.api.repository.PagoRepository;
import com.cafearoma.api.repository.PedidoRepository;
import com.cafearoma.api.repository.ProductoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PagoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void aprobarPagoCorrectamente() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setCliente(cliente);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(new BigDecimal("90000"));

        PagoRequest request = new PagoRequest();
        request.setIdPedido(1L);
        request.setMetodoPago("TARJETA");
        request.setAprobado(true);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        when(pagoRepository
                .existsByPedidoIdPedidoAndEstadoPago(1L, "APROBADO"))
                .thenReturn(false);

        when(pagoRepository.save(any(Pago.class)))
                .thenAnswer(invocation -> {
                    Pago pago = invocation.getArgument(0);
                    pago.setIdPago(1L);
                    return pago;
                });

        PagoResponse response =
                pagoService.simularPago(usuario, request);

        assertNotNull(response);
        assertEquals(1L, response.getIdPago());
        assertEquals(1L, response.getIdPedido());
        assertEquals("TARJETA", response.getMetodoPago());
        assertEquals(new BigDecimal("90000"), response.getValorPagado());
        assertEquals("APROBADO", response.getEstadoPago());
        assertNotNull(response.getFechaPago());

        // El pedido debe quedar pagado.
        assertEquals("PAGADO", pedido.getEstado());

        verify(pedidoRepository).save(pedido);
        verify(pagoRepository).save(any(Pago.class));

        // En un pago aprobado no se devuelve stock.
        verify(detallePedidoRepository, never())
                .findByPedidoIdPedido(anyLong());

        verify(productoRepository, never())
                .save(any(Producto.class));
    }

    @Test
    void rechazarPagoDebeCancelarPedidoYDevolverStock() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        Pedido pedido = new Pedido();
        pedido.setIdPedido(1L);
        pedido.setCliente(cliente);
        pedido.setEstado("PENDIENTE");
        pedido.setTotal(new BigDecimal("90000"));

        Producto producto = new Producto();
        producto.setIdProducto(4L);
        producto.setNombreProducto("Café Premium");
        producto.setStock(8);

        DetallePedido detalle = new DetallePedido();
        detalle.setIdDetalle(1L);
        detalle.setPedido(pedido);
        detalle.setProducto(producto);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(new BigDecimal("45000"));
        detalle.setSubtotal(new BigDecimal("90000"));

        PagoRequest request = new PagoRequest();
        request.setIdPedido(1L);
        request.setMetodoPago("TARJETA");
        request.setAprobado(false);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(pedidoRepository.findById(1L))
                .thenReturn(Optional.of(pedido));

        when(pagoRepository
                .existsByPedidoIdPedidoAndEstadoPago(1L, "APROBADO"))
                .thenReturn(false);

        when(detallePedidoRepository.findByPedidoIdPedido(1L))
                .thenReturn(List.of(detalle));

        when(pagoRepository.save(any(Pago.class)))
                .thenAnswer(invocation -> {
                    Pago pago = invocation.getArgument(0);
                    pago.setIdPago(2L);
                    return pago;
                });

        PagoResponse response =
                pagoService.simularPago(usuario, request);

        assertNotNull(response);
        assertEquals(2L, response.getIdPago());
        assertEquals(1L, response.getIdPedido());
        assertEquals("RECHAZADO", response.getEstadoPago());
        assertEquals(new BigDecimal("90000"), response.getValorPagado());

        // El pedido debe quedar cancelado.
        assertEquals("CANCELADO", pedido.getEstado());

        // El stock era 8 y se devuelven 2 unidades.
        assertEquals(10, producto.getStock());

        verify(detallePedidoRepository)
                .findByPedidoIdPedido(1L);

        verify(productoRepository)
                .save(producto);

        verify(pedidoRepository)
                .save(pedido);

        verify(pagoRepository)
                .save(any(Pago.class));
    }
}