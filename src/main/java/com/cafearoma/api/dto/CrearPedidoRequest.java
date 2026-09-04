package com.cafearoma.api.dto;

public class CrearPedidoRequest {

    private Long idDireccionEnvio;

    public CrearPedidoRequest() {
    }

    public Long getIdDireccionEnvio() {
        return idDireccionEnvio;
    }

    public void setIdDireccionEnvio(Long idDireccionEnvio) {
        this.idDireccionEnvio = idDireccionEnvio;
    }
}