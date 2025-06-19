package com.UTP.Certificado.controller;


import com.UTP.Certificado.dto.CertificadoRequestDTO;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.repository.CertificadoRepository;
import com.UTP.Certificado.service.CertificadoService;
import com.UTP.Certificado.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certificados")
public class CertificadoController {

    @Autowired
    private CertificadoService certificadoService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private CertificadoRepository certificadoRepository;




    @PostMapping
    public ResponseEntity<Certificado> crearCertificado(@RequestBody CertificadoRequestDTO dto) {
        Certificado nuevoCertificado = certificadoService.crearCertificado(dto);
        return ResponseEntity.ok(nuevoCertificado);
    }


    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> descargarCertificadoPdf(@PathVariable Long id) {
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));

        byte[] pdfBytes = pdfGeneratorService.generarPdfCertificado(certificado);

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=certificado_" + id + ".pdf")
                .contentType(org.springframework.http.MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Certificado> getCertificadoById(@PathVariable Long id) {
        Certificado certificado = certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));
        return ResponseEntity.ok(certificado);
    }


    @GetMapping("/codigo/{codigoVerificacion}")
    public ResponseEntity<Certificado> buscarPorCodigoVerificacion(@PathVariable String codigoVerificacion) {
        return certificadoRepository.findByCodigoVerificacion(codigoVerificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<List<Certificado>> getAllCertificados() {
        List<Certificado> certificados = certificadoRepository.findAll();
        return ResponseEntity.ok(certificados);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCertificado(@PathVariable Long id) {
        certificadoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<Certificado> actualizarCertificado(@PathVariable Long id, @RequestBody CertificadoRequestDTO dto) {
        Certificado certificadoExistente = certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));

        certificadoExistente.setNombreEstudiante(dto.getNombreEstudiante());
        certificadoExistente.setCurso(dto.getCurso());
        certificadoExistente.setNota(dto.getNota());
        certificadoExistente.setFechaEmision(dto.getFechaEmision());
        certificadoExistente.setDescripcion(dto.getDescripcion());
        certificadoExistente.setHabilidades(dto.getHabilidades());

        certificadoRepository.save(certificadoExistente);

        return ResponseEntity.ok(certificadoExistente);
    }

}
