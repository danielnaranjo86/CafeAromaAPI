package com.cafearoma.api.dto;

import java.math.BigDecimal;

public class ProductoResponse {

    private Long idProducto;
    private String nombreProducto;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private String imagenUrl;
    private Boolean activo;
    private CategoriaResponse categoria;

    public ProductoResponse(
            Long idProducto,
            String nombreProducto,
            String descripcion,
            BigDecimal precio,
            Integer stock,
            String imagenUrl,
            Boolean activo,
            CategoriaResponse categoria
    ) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.imagenUrl = imagenUrl;
        this.activo = activo;
        this.categoria = categoria;
    }

    public Long getIdProducto() {
        return idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public Integer getStock() {
        return stock;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public Boolean getActivo() {
        return activo;
    }

    public CategoriaResponse getCategoria() {
        return categoria;
    }
}