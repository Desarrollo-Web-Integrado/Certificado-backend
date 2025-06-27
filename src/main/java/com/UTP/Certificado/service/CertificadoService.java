package com.UTP.Certificado.service;

import com.UTP.Certificado.dto.CertificadoRequestDTO;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.model.Usuario;
import com.UTP.Certificado.repository.CertificadoRepository;
import com.UTP.Certificado.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CertificadoService {

    @Autowired
    private CertificadoRepository certificadoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    public Certificado crearCertificado(CertificadoRequestDTO dto) {

        // Buscar al estudiante usando el correo
        Usuario estudiante = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado con correo: " + dto.getCorreo()));

        Certificado certificado = new Certificado();
        certificado.setEstudiante(estudiante);
        certificado.setCurso(dto.getCurso());
        certificado.setNota(dto.getNota());
        certificado.setDescripcion(dto.getDescripcion());
        certificado.setHabilidades(dto.getHabilidades());

        certificado.setFechaEmision(dto.getFechaEmision() != null ? dto.getFechaEmision() : LocalDate.now());
        certificado.setCodigoVerificacion(generarCodigoUnico());

        return certificadoRepository.save(certificado);
    }

    private String generarCodigoUnico() {
        return UUID.randomUUID().toString();
    }
}


