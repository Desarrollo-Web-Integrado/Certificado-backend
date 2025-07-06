package com.UTP.Certificado.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CertificadoPublicoDTO {

    private String nombreCompleto;
    private String curso;
    private Double nota;
    private String descripcion;
    private List<String> habilidades;
    private LocalDate fechaEmision;
    private String codigoVerificacion;
}
