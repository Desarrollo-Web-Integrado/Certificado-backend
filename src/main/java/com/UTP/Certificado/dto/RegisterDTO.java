package com.UTP.Certificado.dto;

import com.UTP.Certificado.model.Rol;

public class RegisterDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String clave;
    private String rol; // luego se convierte a Enum

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public Rol getRolEnum() {
        return Rol.valueOf(this.rol.toUpperCase());
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}

