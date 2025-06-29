package com.UTP.Certificado.dto;

import com.UTP.Certificado.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class LoginResponseDTO {
    private String correo;
    private Rol rol;
}
