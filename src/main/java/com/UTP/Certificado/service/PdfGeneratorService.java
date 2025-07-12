package com.UTP.Certificado.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
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
import java.util.stream.Collectors;


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

            BaseFont baseFont = BaseFont.createFont("fonts/PinyonScript-Regular.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            Font nombreFont = new Font(baseFont, 53.3f);




            // A4 horizontal pero más bajo (solo 500 de alto)
            Rectangle tamañoCertificado = new Rectangle(PageSize.A4.getHeight(), 500f);
            Document documento = new Document(tamañoCertificado, 0, 0, 0, 0);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(documento, out);
            documento.open();

            // 🖼️ Fondo
            Image fondo = cargarImagenDesdeRecursos("certificado.png");

            // Escalar fondo al tamaño exacto del documento
            fondo.scaleAbsolute(tamañoCertificado.getWidth(), tamañoCertificado.getHeight());
            fondo.setAbsolutePosition(0, 0);
            // Añadir imagen debajo del contenido
            writer.getDirectContentUnder().addImage(fondo);

            // 🎨 Fuentes
            Font tituloFont = new Font(Font.HELVETICA, 28, Font.BOLD);
            Font seccionFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font textoFont = new Font(Font.HELVETICA, 14);


            // 📍 Coordenadas base
            float centroX = tamañoCertificado.getWidth() / 2;

            // 🧑‍🎓 Estudiante
            Usuario estudiante = certificado.getEstudiante();

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase(estudiante.getNombre() + " " + estudiante.getApellido(), nombreFont),
                    460, 295, 0);

            // 🧑‍🏫 Curso (con ajuste de ancho y salto de línea)
            ColumnText cursoText = new ColumnText(writer.getDirectContent());
            cursoText.setSimpleColumn(
                    460,   // x izquierda Fija
                    224,   // y inferior
                    740,   // x derecha límite
                    247    // y superior
            );
            cursoText.setAlignment(Element.ALIGN_LEFT); // 👈 El inicio queda fijo
            cursoText.addText(new Phrase(certificado.getCurso(), textoFont));
            cursoText.go();


            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase("" + certificado.getNota(), textoFont),
                    437, 215, 0);

            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase("" + certificado.getFechaEmision(), textoFont),
                    537, 352, 0);

            // 📉 Reducir el tamaño de fuente de habilidades
            Font textoHabilidadFont = new Font(Font.HELVETICA, 9);

            // 🔧 Unir habilidades en una sola línea
            String habilidadesTexto = certificado.getHabilidades().stream()
                    .map(h -> "• " + h)
                    .collect(Collectors.joining("   "));

            // 📍 Mostrar habilidades como columna fija , esto me dio muchos problemas xd x la longitud de estas
            ColumnText habilidadesText = new ColumnText(writer.getDirectContent());
            habilidadesText.setSimpleColumn(
                    370,   // x izquierda fija
                    180,   // y inferior
                    750,   // x derecha límite
                    200    // y superior
            );
            habilidadesText.setAlignment(Element.ALIGN_LEFT); // 👈 Alineación a la izquierda desde punto fijo
            habilidadesText.addText(new Phrase(habilidadesTexto, textoHabilidadFont));
            habilidadesText.go();



            // 🔍 QR
            ColumnText.showTextAligned(writer.getDirectContent(), Element.ALIGN_CENTER,
                    new Phrase("Verifica este certificado escaneando el código QR:", textoFont),
                    -380, 70, 0);

            String qrUrl = baseUrl + "/verification/certificado/?link=" + certificado.getCodigoVerificacion();
            Image qrImage = generarCodigoQR(qrUrl);
            qrImage.scaleToFit(100, 100);
            qrImage.setAbsolutePosition(centroX - 320, 300); // centrado horizontal
            documento.add(qrImage);



             //🖊️ Firma
            try {
                Image firma = cargarImagenDesdeRecursos("firma.png");

                // 🔧 Ajusta el tamaño de la firma
                firma.scaleToFit(140, 80);

                // 📍 Posición absoluta
                float xFirma = 347f; // Posición horizontal desde la izquierda
                float yFirma = 70f;  // Posición vertical desde abajo
                firma.setAbsolutePosition(xFirma, yFirma);

                // 🖼️ Añadir imagen
                documento.add(firma);

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

