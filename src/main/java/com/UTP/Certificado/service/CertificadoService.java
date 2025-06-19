package com.UTP.Certificado.service;

import com.UTP.Certificado.dto.CertificadoRequestDTO;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.repository.CertificadoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    public Certificado crearCertificado(CertificadoRequestDTO dto) {
        Certificado certificado = new Certificado();
        certificado.setNombreEstudiante(dto.getNombreEstudiante());
        certificado.setCodigo(dto.getCodigo());
        certificado.setCurso(dto.getCurso());
        certificado.setNota(dto.getNota());
        // NUEVOS CAMPOS
        certificado.setDescripcion(dto.getDescripcion());
        certificado.setHabilidades(dto.getHabilidades());
        certificado.setFechaEmision(dto.getFechaEmision());
        certificado.setCodigoVerificacion(generarCodigoUnico());

        return certificadoRepository.save(certificado);
    }

    private String generarCodigoUnico() {
        return UUID.randomUUID().toString(); // Sirve como código de verificación posible QR
    }
}
