package com.UTP.Certificado.repository;

import com.UTP.Certificado.model.Certificado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificadoRepository extends JpaRepository<Certificado, Long> {

    Optional<Certificado> findByCodigoVerificacion(String codigoVerificacion);
}
