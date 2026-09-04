package com.cafearoma.api.dto;

public class PerfilResponse {

    private Long idUsuario;
    private String nombre;
    private String correo;
    private String rol;
    private String telefono;

    public PerfilResponse(Long idUsuario, String nombre, String correo, String rol, String telefono) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.correo = correo;
        this.rol = rol;
        this.telefono = telefono;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getRol() {
        return rol;
    }

    public String getTelefono() {
        return telefono;
    }
}