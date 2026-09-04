package com.cafearoma.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PagoResponse {

    private Long idPago;
    private Long idPedido;
    private String metodoPago;
    private BigDecimal valorPagado;
    private String estadoPago;
    private LocalDateTime fechaPago;

    public PagoResponse(
            Long idPago,
            Long idPedido,
            String metodoPago,
            BigDecimal valorPagado,
            String estadoPago,
            LocalDateTime fechaPago
    ) {
        this.idPago = idPago;
        this.idPedido = idPedido;
        this.metodoPago = metodoPago;
        this.valorPagado = valorPagado;
        this.estadoPago = estadoPago;
        this.fechaPago = fechaPago;
    }

    public Long getIdPago() {
        return idPago;
    }

    public Long getIdPedido() {
        return idPedido;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public BigDecimal getValorPagado() {
        return valorPagado;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }
}