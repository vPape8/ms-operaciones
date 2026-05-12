package com.cordytech.ms_operaciones.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.service.ReporteService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Override
    public ResponseEntity<byte[]> generarReportePDF(List<BoletaResponse> boletas) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Aquí iría la implementación real con iText
            // Por ahora, generamos un PDF simple como ejemplo
            String contenido = generarContenidoPDFReporte(boletas);
            outputStream.write(contenido.getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "reporte_boletas.pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte PDF", e);
        }
    }

    @Override
    public ResponseEntity<byte[]> generarReporteExcel(List<BoletaResponse> boletas) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            // Aquí iría la implementación real con Apache POI
            // Por ahora, generamos un CSV simple como ejemplo
            String contenido = generarContenidoCSVReporte(boletas);
            outputStream.write(contenido.getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "reporte_boletas.xlsx");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando reporte Excel", e);
        }
    }

    @Override
    public ResponseEntity<byte[]> generarBoletaPDF(BoletaResponse boleta) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String contenido = generarContenidoPDFBoleta(boleta);
            outputStream.write(contenido.getBytes());
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "boleta_" + boleta.getIdBoleta() + ".pdf");
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Error generando boleta PDF", e);
        }
    }

    @Override
    public void enviarBoletaPorEmail(BoletaResponse boleta, String emailDestino) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setFrom(emailFrom);
            helper.setTo(emailDestino);
            helper.setSubject("Boleta Portuaria - " + boleta.getIdBoleta());
            
            String textoEmail = generarTextoEmailBoleta(boleta);
            helper.setText(textoEmail, true);
            
            // Adjuntar el PDF de la boleta
            ResponseEntity<byte[]> pdfResponse = generarBoletaPDF(boleta);
            ByteArrayResource pdfResource = new ByteArrayResource(pdfResponse.getBody(), "boleta_" + boleta.getIdBoleta() + ".pdf");
            helper.addAttachment("boleta_" + boleta.getIdBoleta() + ".pdf", pdfResource);
            
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Error enviando email", e);
        }
    }

    private String generarContenidoPDFReporte(List<BoletaResponse> boletas) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("REPORTE DE BOLETAS PORTUARIAS\n\n");
        contenido.append("Fecha de generación: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        double totalGeneral = 0.0;
        
        for (BoletaResponse boleta : boletas) {
            contenido.append("ID Boleta: ").append(boleta.getIdBoleta()).append("\n");
            contenido.append("Fecha Emisión: ").append(boleta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            contenido.append("Código Buque: ").append(boleta.getCodBuque()).append("\n");
            contenido.append("ID Puerto: ").append(boleta.getIdPuerto()).append("\n");
            contenido.append("Monto: ").append(currencyFormat.format(boleta.getMonto())).append("\n");
            contenido.append("Estado: ").append(boleta.getEstado()).append("\n");
            contenido.append("----------------------------------------\n");
            totalGeneral += boleta.getMonto();
        }
        
        contenido.append("\nTOTAL GENERAL: ").append(currencyFormat.format(totalGeneral)).append("\n");
        return contenido.toString();
    }

    private String generarContenidoCSVReporte(List<BoletaResponse> boletas) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("ID Boleta,Fecha Emisión,Buque,Puerto,Monto,Estado,Días Estancia,Tipo Servicio\n");
        
        for (BoletaResponse boleta : boletas) {
            contenido.append(boleta.getIdBoleta()).append(",");
            contenido.append(boleta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(",");
            contenido.append(boleta.getCodBuque()).append(",");
            contenido.append(boleta.getIdPuerto()).append(",");
            contenido.append(boleta.getMonto()).append(",");
            contenido.append(boleta.getEstado()).append(",");
            contenido.append(boleta.getDiasEstancia()).append(",");
            contenido.append(boleta.getTipoServicio()).append("\n");
        }
        
        return contenido.toString();
    }

    private String generarContenidoPDFBoleta(BoletaResponse boleta) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("BOLETA PORTUARIA\n\n");
        contenido.append("ID: ").append(boleta.getIdBoleta()).append("\n");
        contenido.append("Fecha Emisión: ").append(boleta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        contenido.append("DATOS DEL BUQUE:\n");
        contenido.append("Código: ").append(boleta.getCodBuque()).append("\n");
        
        contenido.append("\nDATOS DEL PUERTO:\n");
        contenido.append("ID Puerto: ").append(boleta.getIdPuerto()).append("\n");
        
        contenido.append("\nDETALLES DEL SERVICIO:\n");
        contenido.append("Días de Estancia: ").append(boleta.getDiasEstancia()).append("\n");
        contenido.append("Tipo de Servicio: ").append(boleta.getTipoServicio()).append("\n");
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        contenido.append("\nMONTO A PAGAR: ").append(currencyFormat.format(boleta.getMonto())).append("\n");
        contenido.append("Estado: ").append(boleta.getEstado()).append("\n");
        
        if (boleta.getObservaciones() != null && !boleta.getObservaciones().trim().isEmpty()) {
            contenido.append("\nObservaciones: ").append(boleta.getObservaciones()).append("\n");
        }
        
        return contenido.toString();
    }

    private String generarTextoEmailBoleta(BoletaResponse boleta) {
        StringBuilder texto = new StringBuilder();
        texto.append("<html><body>");
        texto.append("<h2>Boleta Portuaria Generada</h2>");
        texto.append("<p>Estimado/a,</p>");
        texto.append("<p>Se ha generado una nueva boleta portuaria con los siguientes detalles:</p>");
        
        texto.append("<table border='1' style='border-collapse: collapse; width: 100%;'>");
        texto.append("<tr><td><strong>ID Boleta:</strong></td><td>").append(boleta.getIdBoleta()).append("</td></tr>");
        texto.append("<tr><td><strong>Fecha Emisión:</strong></td><td>").append(boleta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</td></tr>");
        
        texto.append("<tr><td><strong>Código Buque:</strong></td><td>").append(boleta.getCodBuque()).append("</td></tr>");
        texto.append("<tr><td><strong>ID Puerto:</strong></td><td>").append(boleta.getIdPuerto()).append("</td></tr>");
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        texto.append("<tr><td><strong>Monto:</strong></td><td>").append(currencyFormat.format(boleta.getMonto())).append("</td></tr>");
        texto.append("<tr><td><strong>Estado:</strong></td><td>").append(boleta.getEstado()).append("</td></tr>");
        texto.append("</table>");
        
        texto.append("<p>El PDF de la boleta se adjunta a este correo.</p>");
        texto.append("<p>Saludos cordiales,<br>Sistema de Operaciones Portuarias</p>");
        texto.append("</body></html>");
        
        return texto.toString();
    }
}
