package com.UTP.Certificado.controller;


import com.UTP.Certificado.dto.CertificadoPublicoDTO;
import com.UTP.Certificado.dto.CertificadoRequestDTO;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.model.Usuario;
import com.UTP.Certificado.repository.CertificadoRepository;
import com.UTP.Certificado.repository.UsuarioRepository;
import com.UTP.Certificado.service.CertificadoService;
import com.UTP.Certificado.service.PdfGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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

    @Autowired
    private UsuarioRepository usuarioRepository;

    //  Crear nuevo certificado
    @PostMapping
    public ResponseEntity<Certificado> crearCertificado(@RequestBody CertificadoRequestDTO dto) {
        Certificado nuevoCertificado = certificadoService.crearCertificado(dto);
        return ResponseEntity.ok(nuevoCertificado);
    }

    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//

    //  Descargar PDF del certificado
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

    //  Obtener certificado por ID
    @GetMapping("/{id}")
    public ResponseEntity<Certificado> getCertificadoById(@PathVariable Long id) {
        return certificadoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Obtener  certificados por correo del estudiante
    @GetMapping("/estudiante")
    public ResponseEntity<List<CertificadoPublicoDTO>> obtenerCertificadosPorCorreo(@RequestParam String correo) {
        List<Certificado> certificados = certificadoRepository.findByEstudianteCorreo(correo);

        if (certificados.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<CertificadoPublicoDTO> dtos = certificados.stream()
                .map(certificado -> new CertificadoPublicoDTO(
                        certificado.getEstudiante().getNombre() + " " + certificado.getEstudiante().getApellido(),
                        certificado.getCurso(),
                        certificado.getNota(),
                        certificado.getDescripcion(),
                        certificado.getHabilidades(),
                        certificado.getFechaEmision(),
                        certificado.getCodigoVerificacion()
                )).toList();

        return ResponseEntity.ok(dtos);
    }


    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//


    //Devuelve un JSON con los datos del certificado
    @GetMapping("/codigo/{codigoVerificacion}")
    public ResponseEntity<CertificadoPublicoDTO> buscarPorCodigoVerificacion(@PathVariable String codigoVerificacion) {
        return certificadoRepository.findByCodigoVerificacion(codigoVerificacion)
                .map(certificado -> {
                    CertificadoPublicoDTO dto = new CertificadoPublicoDTO(
                            certificado.getEstudiante().getNombre() + " " + certificado.getEstudiante().getApellido(),
                            certificado.getCurso(),
                            certificado.getNota(),
                            certificado.getDescripcion(),
                            certificado.getHabilidades(),
                            certificado.getFechaEmision(),
                            certificado.getCodigoVerificacion()
                    );
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    //Ideal para escanear un QR y descargar o muestrar el PDF
    @GetMapping("/codigo/{codigoVerificacion}/pdf")
    public ResponseEntity<byte[]> descargarCertificadoPdfPorCodigo(@PathVariable String codigoVerificacion) {
        Certificado certificado = certificadoRepository.findByCodigoVerificacion(codigoVerificacion)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));

        byte[] pdfBytes = pdfGeneratorService.generarPdfCertificado(certificado);

        return ResponseEntity.ok()
                .header("Content-Disposition", "inline; filename=certificado.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }


    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//



    //  Obtener todos los certificados (admin)
    @GetMapping
    public ResponseEntity<List<Certificado>> getAllCertificados() {
        return ResponseEntity.ok(certificadoRepository.findAll());
    }

    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//

    //  Eliminar un certificado
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCertificado(@PathVariable Long id) {
        if (!certificadoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        certificadoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//
    //------------------------------------------------------------------------------------------------//


    //  Actualizar certificado
    @PutMapping("/{id}")
    public ResponseEntity<Certificado> actualizarCertificado(
            @PathVariable Long id,
            @RequestBody CertificadoRequestDTO dto) {

        Certificado certificadoExistente = certificadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Certificado no encontrado"));

        Usuario estudiante = usuarioRepository.findByCorreo(dto.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + dto.getCorreo()));


        certificadoExistente.setEstudiante(estudiante);
        certificadoExistente.setCurso(dto.getCurso());
        certificadoExistente.setNota(dto.getNota());
        certificadoExistente.setFechaEmision(
                dto.getFechaEmision() != null ? dto.getFechaEmision() : LocalDate.now()
        );
        certificadoExistente.setDescripcion(dto.getDescripcion());
        certificadoExistente.setHabilidades(dto.getHabilidades());

        return ResponseEntity.ok(certificadoRepository.save(certificadoExistente));
    }
}
