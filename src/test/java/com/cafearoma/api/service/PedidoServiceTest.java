package com.cafearoma.api.service;

import com.cafearoma.api.dto.CrearPedidoRequest;
import com.cafearoma.api.dto.PedidoResponse;
import com.cafearoma.api.model.*;
import com.cafearoma.api.repository.*;
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
class PedidoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private DetallePedidoRepository detallePedidoRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private DireccionEnvioRepository direccionEnvioRepository;

    @InjectMocks
    private PedidoService pedidoService;

    @Test
    void crearPedidoDesdeCarritoCorrectamente() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        DireccionEnvio direccion = new DireccionEnvio();
        direccion.setIdDireccion(1L);
        direccion.setCliente(cliente);
        direccion.setDireccion("Calle 123 # 45-67");
        direccion.setCiudad("Bogotá");
        direccion.setDepartamento("Cundinamarca");
        direccion.setPais("Colombia");
        direccion.setReferencia("Apto 302");
        direccion.setPredeterminada(true);
        direccion.setActiva(true);

        Producto producto = new Producto();
        producto.setIdProducto(4L);
        producto.setNombreProducto("Café Premium");
        producto.setPrecio(new BigDecimal("45000"));
        producto.setStock(10);
        producto.setActivo(true);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);
        carrito.setCliente(cliente);
        carrito.setEstado("ACTIVO");

        CarritoItem item = new CarritoItem();
        item.setIdItem(1L);
        item.setCarrito(carrito);
        item.setProducto(producto);
        item.setCantidad(2);
        item.setPrecioUnitario(new BigDecimal("45000"));

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdDireccionEnvio(1L);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(direccionEnvioRepository
                .findByIdDireccionAndClienteIdClienteAndActivaTrue(1L, 1L))
                .thenReturn(Optional.of(direccion));

        when(carritoRepository
                .findByClienteIdClienteAndEstado(1L, "ACTIVO"))
                .thenReturn(Optional.of(carrito));

        when(carritoItemRepository.findByCarritoIdCarrito(1L))
                .thenReturn(List.of(item));

        when(pedidoRepository.save(any(Pedido.class)))
                .thenAnswer(invocation -> {
                    Pedido pedido = invocation.getArgument(0);
                    pedido.setIdPedido(1L);
                    return pedido;
                });

        DetallePedido detalleGuardado = new DetallePedido();
        detalleGuardado.setIdDetalle(1L);
        detalleGuardado.setProducto(producto);
        detalleGuardado.setCantidad(2);
        detalleGuardado.setPrecioUnitario(new BigDecimal("45000"));
        detalleGuardado.setSubtotal(new BigDecimal("90000"));

        when(detallePedidoRepository.findByPedidoIdPedido(1L))
                .thenReturn(List.of(detalleGuardado));

        PedidoResponse response =
                pedidoService.crearPedidoDesdeCarrito(usuario, request);

        assertNotNull(response);
        assertEquals(1L, response.getIdPedido());
        assertEquals("PENDIENTE", response.getEstado());
        assertEquals(new BigDecimal("90000"), response.getTotal());

        assertNotNull(response.getDireccionEnvio());
        assertEquals(
                "Calle 123 # 45-67",
                response.getDireccionEnvio().getDireccion()
        );

        assertEquals(1, response.getItems().size());
        assertEquals(
                "Café Premium",
                response.getItems().get(0).getNombreProducto()
        );

        // El stock debe reducirse de 10 a 8.
        assertEquals(8, producto.getStock());

        // El carrito debe quedar cerrado.
        assertEquals("CERRADO", carrito.getEstado());

        verify(pedidoRepository).save(any(Pedido.class));
        verify(detallePedidoRepository).save(any(DetallePedido.class));
        verify(productoRepository).save(producto);
        verify(carritoRepository).save(carrito);
    }

    @Test
    void noDebeCrearPedidoSiCarritoEstaVacio() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        DireccionEnvio direccion = new DireccionEnvio();
        direccion.setIdDireccion(1L);
        direccion.setCliente(cliente);
        direccion.setDireccion("Calle 123 # 45-67");
        direccion.setCiudad("Bogotá");
        direccion.setPais("Colombia");
        direccion.setActiva(true);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);
        carrito.setCliente(cliente);
        carrito.setEstado("ACTIVO");

        CrearPedidoRequest request = new CrearPedidoRequest();
        request.setIdDireccionEnvio(1L);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(direccionEnvioRepository
                .findByIdDireccionAndClienteIdClienteAndActivaTrue(1L, 1L))
                .thenReturn(Optional.of(direccion));

        when(carritoRepository
                .findByClienteIdClienteAndEstado(1L, "ACTIVO"))
                .thenReturn(Optional.of(carrito));

        when(carritoItemRepository.findByCarritoIdCarrito(1L))
                .thenReturn(List.of());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> pedidoService.crearPedidoDesdeCarrito(usuario, request)
        );

        assertEquals(
                "El carrito está vacío.",
                exception.getMessage()
        );

        verify(pedidoRepository, never())
                .save(any(Pedido.class));

        verify(detallePedidoRepository, never())
                .save(any(DetallePedido.class));
    }
}