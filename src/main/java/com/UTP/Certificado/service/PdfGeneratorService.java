package com.UTP.Certificado.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.UTP.Certificado.model.Certificado;
import com.UTP.Certificado.model.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;





@Service
public class PdfGeneratorService {

    // 🔗 Se inyecta la URL base del frontend desde application.properties
    @Value("${app.frontend.url}")
    private String baseUrl;

    private Image generarCodigoQR(String texto) throws WriterException, IOException, BadElementException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(texto, BarcodeFormat.QR_CODE, 200, 200);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", pngOutputStream);
        return Image.getInstance(pngOutputStream.toByteArray());
    }



    private Image cargarImagenDesdeRecursos(String nombreArchivo) throws IOException, BadElementException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/" + nombreArchivo);
        if (inputStream == null) {
            throw new FileNotFoundException("No se encontró la imagen: " + nombreArchivo);
        }
        byte[] bytes = inputStream.readAllBytes();
        return Image.getInstance(bytes);
    }




    public byte[] generarPdfCertificado(Certificado certificado) {
        try {
            Document documento = new Document(PageSize.A4);
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            PdfWriter.getInstance(documento, out);
            documento.open();

            // Fuente y estilo
            Font tituloFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font seccionFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font textoFont = new Font(Font.HELVETICA, 14);


            // Logo (opcional)
            try {
                Image logo = cargarImagenDesdeRecursos("logo.png");
                logo.scaleToFit(100, 100);
                logo.setAlignment(Image.ALIGN_LEFT);
                documento.add(logo);
            } catch (Exception e) {
                System.out.println("No se pudo cargar el logo: " + e.getMessage());
            }

            // Título
            Paragraph titulo = new Paragraph("Certificado de Notas", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            documento.add(titulo);
            documento.add(Chunk.NEWLINE);
            //documento.add(new Paragraph("\n"));

            // Obtener estudiante
            Usuario estudiante = certificado.getEstudiante();

            // Información del estudiante
            documento.add(new Paragraph("👨‍🎓 Estudiante: " + estudiante.getNombre() + " " + estudiante.getApellido(), textoFont));
            documento.add(new Paragraph("✉️ Correo: " + estudiante.getCorreo(), textoFont));
            documento.add(new Paragraph("📚 Curso: " + certificado.getCurso(), textoFont));
            documento.add(new Paragraph("📝 Nota Final: " + certificado.getNota(), textoFont));
            documento.add(new Paragraph("📅 Fecha de Emisión: " + certificado.getFechaEmision(), textoFont));


            documento.add(Chunk.NEWLINE);

            // Descripción
            documento.add(new Paragraph("📌 Descripción:", seccionFont));
            documento.add(new Paragraph(certificado.getDescripcion(), textoFont));
            documento.add(Chunk.NEWLINE);

            // Habilidades
            documento.add(new Paragraph("🛠️ Habilidades obtenidas:", seccionFont));
            for (String habilidad : certificado.getHabilidades()) {
                documento.add(new Paragraph("• " + habilidad, textoFont));
            }
            documento.add(Chunk.NEWLINE);



            // 👽 Agregar código QR *url configurable en application.properties
            documento.add(new Paragraph("🔍 Verifica este certificado escaneando el código QR:", textoFont));


            String qrUrl = baseUrl + "/verification/certificado/?link=" + certificado.getCodigoVerificacion();
            Image qrImage = generarCodigoQR(qrUrl);


            qrImage.setAlignment(Element.ALIGN_CENTER);
            qrImage.scaleToFit(140, 140); //tamaño del QR
            documento.add(qrImage);



            // Firma
            try {
                Image firma = cargarImagenDesdeRecursos("firma.png");
                firma.scaleToFit(100, 50);
                firma.setAlignment(Image.ALIGN_RIGHT);
                documento.add(firma);
                Paragraph nombreFirma = new Paragraph("Director Académico", textoFont);
                nombreFirma.setAlignment(Element.ALIGN_RIGHT);
                documento.add(nombreFirma);
            } catch (Exception e) {
                System.out.println("No se pudo cargar la firma: " + e.getMessage());
            }

            documento.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF del certificado", e);
        }
    }
}

