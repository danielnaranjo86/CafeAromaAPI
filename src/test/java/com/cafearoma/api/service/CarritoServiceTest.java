package com.cafearoma.api.service;

import com.cafearoma.api.dto.AgregarCarritoRequest;
import com.cafearoma.api.dto.CarritoResponse;
import com.cafearoma.api.model.Carrito;
import com.cafearoma.api.model.CarritoItem;
import com.cafearoma.api.model.Cliente;
import com.cafearoma.api.model.Producto;
import com.cafearoma.api.model.Usuario;
import com.cafearoma.api.repository.CarritoItemRepository;
import com.cafearoma.api.repository.CarritoRepository;
import com.cafearoma.api.repository.ClienteRepository;
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
class CarritoServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private CarritoRepository carritoRepository;

    @Mock
    private CarritoItemRepository carritoItemRepository;

    @InjectMocks
    private CarritoService carritoService;

    @Test
    void agregarProductoAlCarritoCorrectamente() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);
        carrito.setCliente(cliente);
        carrito.setEstado("ACTIVO");

        Producto producto = new Producto();
        producto.setIdProducto(4L);
        producto.setNombreProducto("Café Premium");
        producto.setPrecio(new BigDecimal("45000"));
        producto.setStock(10);
        producto.setActivo(true);

        AgregarCarritoRequest request = new AgregarCarritoRequest();
        request.setIdProducto(4L);
        request.setCantidad(2);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(carritoRepository.findByClienteIdClienteAndEstado(1L, "ACTIVO"))
                .thenReturn(Optional.of(carrito));

        when(productoRepository.findById(4L))
                .thenReturn(Optional.of(producto));

        when(carritoItemRepository
                .findByCarritoIdCarritoAndProductoIdProducto(1L, 4L))
                .thenReturn(Optional.empty());

        when(carritoItemRepository.save(any(CarritoItem.class)))
                .thenAnswer(invocation -> {
                    CarritoItem item = invocation.getArgument(0);
                    item.setIdItem(1L);
                    return item;
                });

        when(carritoItemRepository.findByCarritoIdCarrito(1L))
                .thenAnswer(invocation -> {
                    CarritoItem item = new CarritoItem();
                    item.setIdItem(1L);
                    item.setCarrito(carrito);
                    item.setProducto(producto);
                    item.setCantidad(2);
                    item.setPrecioUnitario(new BigDecimal("45000"));

                    return List.of(item);
                });

        CarritoResponse response =
                carritoService.agregarProducto(usuario, request);

        assertNotNull(response);
        assertEquals(1L, response.getIdCarrito());
        assertEquals("ACTIVO", response.getEstado());
        assertEquals(1, response.getItems().size());
        assertEquals(new BigDecimal("90000"), response.getTotal());

        assertEquals(
                "Café Premium",
                response.getItems().get(0).getNombreProducto()
        );

        assertEquals(
                2,
                response.getItems().get(0).getCantidad()
        );

        verify(carritoItemRepository)
                .save(any(CarritoItem.class));
    }

    @Test
    void noDebeAgregarCantidadMayorAlStock() {

        Usuario usuario = new Usuario();
        usuario.setIdUsuario(3L);

        Cliente cliente = new Cliente();
        cliente.setIdCliente(1L);
        cliente.setUsuario(usuario);

        Carrito carrito = new Carrito();
        carrito.setIdCarrito(1L);
        carrito.setCliente(cliente);
        carrito.setEstado("ACTIVO");

        Producto producto = new Producto();
        producto.setIdProducto(4L);
        producto.setNombreProducto("Café Premium");
        producto.setPrecio(new BigDecimal("45000"));
        producto.setStock(5);
        producto.setActivo(true);

        AgregarCarritoRequest request = new AgregarCarritoRequest();
        request.setIdProducto(4L);
        request.setCantidad(10);

        when(clienteRepository.findByUsuarioIdUsuario(3L))
                .thenReturn(Optional.of(cliente));

        when(carritoRepository.findByClienteIdClienteAndEstado(1L, "ACTIVO"))
                .thenReturn(Optional.of(carrito));

        when(productoRepository.findById(4L))
                .thenReturn(Optional.of(producto));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> carritoService.agregarProducto(usuario, request)
        );

        assertEquals(
                "La cantidad solicitada supera el stock disponible.",
                exception.getMessage()
        );

        verify(carritoItemRepository, never())
                .save(any(CarritoItem.class));
    }
}