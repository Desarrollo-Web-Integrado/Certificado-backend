package com.UTP.Certificado.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.UTP.Certificado.model.Certificado;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;

@Service
public class PdfGeneratorService {

    public byte[] generarPdfCertificado(Certificado certificado) {
        try {
            Document documento = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(documento, out);
            documento.open();

            // Fuente y estilo
            Font tituloFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font textoFont = new Font(Font.HELVETICA, 14);

            // Título
            Paragraph titulo = new Paragraph("Certificado de Notas", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);

            documento.add(new Paragraph("\n"));

            // Contenido
            documento.add(new Paragraph("Nombre del Estudiante: " + certificado.getNombreEstudiante(), textoFont));
            documento.add(new Paragraph("Codigo: " + certificado.getCodigo(), textoFont));
            documento.add(new Paragraph("Curso: " + certificado.getCurso(), textoFont));
            documento.add(new Paragraph("Nota Final: " + certificado.getNota(), textoFont));
            documento.add(new Paragraph("Fecha de Emisión: " + certificado.getFechaEmision(), textoFont));
            documento.add(new Paragraph("Código de Verificación: " + certificado.getCodigoVerificacion(), textoFont));

            documento.add(new Paragraph("\nDescripción:", textoFont));
            documento.add(new Paragraph(certificado.getDescripcion(), textoFont));

            documento.add(new Paragraph("\nHabilidades obtenidas:", textoFont));
            for (String habilidad : certificado.getHabilidades()) {
                documento.add(new Paragraph("- " + habilidad, textoFont));
            }


            documento.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del certificado", e);
        }
    }
}
