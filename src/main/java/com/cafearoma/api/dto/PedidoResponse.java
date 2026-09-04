package com.cafearoma.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoResponse {

    private Long idPedido;
    private LocalDateTime fechaPedido;
    private String estado;
    private BigDecimal total;
    private List<PedidoItemResponse> items;
    private DireccionEnvioResponse direccionEnvio;

    public PedidoResponse(
            Long idPedido,
            LocalDateTime fechaPedido,
            String estado,
            BigDecimal total,
            DireccionEnvioResponse direccionEnvio,
            List<PedidoItemResponse> items
    ) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.direccionEnvio = direccionEnvio;
        this.items = items;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }

    public String getEstado() {
        return estado;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public List<PedidoItemResponse> getItems() {
        return items;
    }

    public DireccionEnvioResponse getDireccionEnvio() {
        return direccionEnvio;
    }
}