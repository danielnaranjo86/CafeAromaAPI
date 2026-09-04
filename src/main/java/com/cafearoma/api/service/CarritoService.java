package com.cafearoma.api.service;

import com.cafearoma.api.dto.AgregarCarritoRequest;
import com.cafearoma.api.dto.ActualizarCantidadRequest;
import com.cafearoma.api.dto.CarritoItemResponse;
import com.cafearoma.api.dto.CarritoResponse;
import com.cafearoma.api.model.*;
import com.cafearoma.api.repository.CarritoItemRepository;
import com.cafearoma.api.repository.CarritoRepository;
import com.cafearoma.api.repository.ClienteRepository;
import com.cafearoma.api.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CarritoService {

    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final CarritoItemRepository carritoItemRepository;

    public CarritoService(
            ClienteRepository clienteRepository,
            ProductoRepository productoRepository,
            CarritoRepository carritoRepository,
            CarritoItemRepository carritoItemRepository
    ) {
        this.clienteRepository = clienteRepository;
        this.productoRepository = productoRepository;
        this.carritoRepository = carritoRepository;
        this.carritoItemRepository = carritoItemRepository;
    }

    public CarritoResponse verCarrito(Usuario usuario) {
        Cliente cliente = obtenerCliente(usuario);
        Carrito carrito = obtenerOCrearCarrito(cliente);

        return construirRespuesta(carrito);
    }

    public CarritoResponse agregarProducto(Usuario usuario, AgregarCarritoRequest request) {
        validarCantidad(request.getCantidad());

        Cliente cliente = obtenerCliente(usuario);
        Carrito carrito = obtenerOCrearCarrito(cliente);

        Producto producto = productoRepository.findById(request.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        if (!Boolean.TRUE.equals(producto.getActivo())) {
            throw new IllegalArgumentException("El producto no está disponible.");
        }

        if (request.getCantidad() > producto.getStock()) {
            throw new IllegalArgumentException("La cantidad solicitada supera el stock disponible.");
        }

        CarritoItem item = carritoItemRepository
                .findByCarritoIdCarritoAndProductoIdProducto(carrito.getIdCarrito(), producto.getIdProducto())
                .orElse(null);

        if (item == null) {
            item = new CarritoItem();
            item.setCarrito(carrito);
            item.setProducto(producto);
            item.setCantidad(request.getCantidad());
            item.setPrecioUnitario(producto.getPrecio());
        } else {
            int nuevaCantidad = item.getCantidad() + request.getCantidad();

            if (nuevaCantidad > producto.getStock()) {
                throw new IllegalArgumentException("La cantidad total en el carrito supera el stock disponible.");
            }

            item.setCantidad(nuevaCantidad);
        }

        carritoItemRepository.save(item);

        return construirRespuesta(carrito);
    }

    public CarritoResponse actualizarCantidad(
            Usuario usuario,
            Long idItem,
            ActualizarCantidadRequest request
    ) {
        validarCantidad(request.getCantidad());

        Cliente cliente = obtenerCliente(usuario);
        Carrito carrito = obtenerOCrearCarrito(cliente);

        CarritoItem item = carritoItemRepository.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Ítem del carrito no encontrado."));

        if (!item.getCarrito().getIdCarrito().equals(carrito.getIdCarrito())) {
            throw new IllegalArgumentException("El ítem no pertenece al carrito del usuario.");
        }

        Producto producto = item.getProducto();

        if (request.getCantidad() > producto.getStock()) {
            throw new IllegalArgumentException("La cantidad solicitada supera el stock disponible.");
        }

        item.setCantidad(request.getCantidad());
        carritoItemRepository.save(item);

        return construirRespuesta(carrito);
    }

    public CarritoResponse eliminarItem(Usuario usuario, Long idItem) {
        Cliente cliente = obtenerCliente(usuario);
        Carrito carrito = obtenerOCrearCarrito(cliente);

        CarritoItem item = carritoItemRepository.findById(idItem)
                .orElseThrow(() -> new IllegalArgumentException("Ítem del carrito no encontrado."));

        if (!item.getCarrito().getIdCarrito().equals(carrito.getIdCarrito())) {
            throw new IllegalArgumentException("El ítem no pertenece al carrito del usuario.");
        }

        carritoItemRepository.delete(item);

        return construirRespuesta(carrito);
    }

    @Transactional
    public CarritoResponse vaciarCarrito(Usuario usuario) {
        Cliente cliente = obtenerCliente(usuario);
        Carrito carrito = obtenerOCrearCarrito(cliente);

        carritoItemRepository.deleteByCarritoIdCarrito(carrito.getIdCarrito());

        return construirRespuesta(carrito);
    }

    private Cliente obtenerCliente(Usuario usuario) {
        return clienteRepository.findByUsuarioIdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new IllegalArgumentException("El usuario autenticado no tiene perfil de cliente."));
    }

    private Carrito obtenerOCrearCarrito(Cliente cliente) {
        return carritoRepository.findByClienteIdClienteAndEstado(cliente.getIdCliente(), "ACTIVO")
                .orElseGet(() -> {
                    Carrito carrito = new Carrito();
                    carrito.setCliente(cliente);
                    carrito.setEstado("ACTIVO");
                    return carritoRepository.save(carrito);
                });
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero.");
        }
    }

    private CarritoResponse construirRespuesta(Carrito carrito) {
        List<CarritoItem> items = carritoItemRepository.findByCarritoIdCarrito(carrito.getIdCarrito());

        List<CarritoItemResponse> itemResponses = items.stream()
                .map(this::mapearItem)
                .toList();

        BigDecimal total = itemResponses.stream()
                .map(CarritoItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CarritoResponse(
                carrito.getIdCarrito(),
                carrito.getEstado(),
                itemResponses,
                total
        );
    }

    private CarritoItemResponse mapearItem(CarritoItem item) {
        BigDecimal subtotal = item.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(item.getCantidad()));

        return new CarritoItemResponse(
                item.getIdItem(),
                item.getProducto().getIdProducto(),
                item.getProducto().getNombreProducto(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                subtotal
        );
    }
}