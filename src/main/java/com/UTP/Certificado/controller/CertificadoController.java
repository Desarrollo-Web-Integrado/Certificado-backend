package com.UTP.Certificado.controller;


import com.UTP.Certificado.dto.CertificadoRequestDTO;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.model.Usuario;
import com.UTP.Certificado.repository.CertificadoRepository;
import com.UTP.Certificado.service.CertificadoService;
import com.UTP.Certificado.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/{codigoVerificacion}")
    public ResponseEntity<Certificado> buscarPorCodigoVerificacion(@PathVariable String codigoVerificacion) {
        return certificadoRepository.findByCodigoVerificacion(codigoVerificacion)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
