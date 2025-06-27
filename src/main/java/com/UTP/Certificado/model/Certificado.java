package com.UTP.Certificado.model;

//import org.springframework.data.annotation.Id; esta importanción no es valida para entidades JPA xd



import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
public class Certificado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String curso;
    private Double nota;
    private LocalDate fechaEmision;
    private String codigoVerificacion;

    @ElementCollection
    private List<String> habilidades;

    private String descripcion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Usuario estudiante;
}

//aca recalco la duda si la 2do importacion de la persistencia fue la correcta o la 1era era la idonea, los errores futuros lo diran