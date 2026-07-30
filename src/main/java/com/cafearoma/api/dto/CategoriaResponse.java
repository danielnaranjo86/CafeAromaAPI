package com.cafearoma.api.dto;

public class CategoriaResponse {

    private Long idCategoria;
    private String nombreCategoria;
    private String descripcion;
    private Boolean activa;

    public CategoriaResponse(Long idCategoria, String nombreCategoria, String descripcion, Boolean activa) {
        this.idCategoria = idCategoria;
        this.nombreCategoria = nombreCategoria;
        this.descripcion = descripcion;
        this.activa = activa;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Boolean getActiva() {
        return activa;
    }
}
