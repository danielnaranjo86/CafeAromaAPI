package com.cafearoma.api.dto;

public class AgregarCarritoRequest {

    private Long idProducto;
    private Integer cantidad;

    public AgregarCarritoRequest() {
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(Long idProducto) {
        this.idProducto = idProducto;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}