package com.UTP.Certificado.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class CertificadoRequestDTO {

    private String correo; // ✅ Buscar al estudiante por su correo único

    private String curso;
    private Double nota;
    private LocalDate fechaEmision;

    private List<String> habilidades;
    private String descripcion;
}
