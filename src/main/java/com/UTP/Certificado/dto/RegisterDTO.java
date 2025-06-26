package com.UTP.Certificado.dto;

import com.UTP.Certificado.model.Rol;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegisterDTO {
    private String nombre;
    private String apellido;
    private String correo;
    private String clave;
    private String rol;


    // ✅ Método para convertir el string a Enum Rol
    public Rol getRolEnum() {
        try {
            return Rol.valueOf(rol.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Rol inválido. Usa ADMINISTRADOR o USER.");
        }
    }
}

