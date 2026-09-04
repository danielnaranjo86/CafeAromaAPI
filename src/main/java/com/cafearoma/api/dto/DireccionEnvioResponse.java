package com.cafearoma.api.dto;

public class DireccionEnvioResponse {

    private Long idDireccion;
    private String direccion;
    private String ciudad;
    private String departamento;
    private String pais;
    private String codigoPostal;
    private String referencia;
    private Boolean predeterminada;
    private Boolean activa;

    public DireccionEnvioResponse(
            Long idDireccion,
            String direccion,
            String ciudad,
            String departamento,
            String pais,
            String codigoPostal,
            String referencia,
            Boolean predeterminada,
            Boolean activa
    ) {
        this.idDireccion = idDireccion;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.departamento = departamento;
        this.pais = pais;
        this.codigoPostal = codigoPostal;
        this.referencia = referencia;
        this.predeterminada = predeterminada;
        this.activa = activa;
    }

    public Long getIdDireccion() {
        return idDireccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getDepartamento() {
        return departamento;
    }

    public String getPais() {
        return pais;
    }

    public String getCodigoPostal() {
        return codigoPostal;
    }

    public String getReferencia() {
        return referencia;
    }

    public Boolean getPredeterminada() {
        return predeterminada;
    }

    public Boolean getActiva() {
        return activa;
    }
}