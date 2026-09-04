package com.cafearoma.api.dto;

import java.math.BigDecimal;
import java.util.List;

public class CarritoResponse {

    private Long idCarrito;
    private String estado;
    private List<CarritoItemResponse> items;
    private BigDecimal total;

    public CarritoResponse(
            Long idCarrito,
            String estado,
            List<CarritoItemResponse> items,
            BigDecimal total
    ) {
        this.idCarrito = idCarrito;
        this.estado = estado;
        this.items = items;
        this.total = total;
    }

    public Long getIdCarrito() {
        return idCarrito;
    }

    public String getEstado() {
        return estado;
    }

    public List<CarritoItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }
}