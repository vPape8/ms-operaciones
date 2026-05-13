package com.cordytech.ms_operaciones.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.cordytech.ms_operaciones.config.RabbitMQConfig;
import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.dto.mq.EmailRequest;
import com.cordytech.ms_operaciones.dto.mq.ReporteRequest;
import com.cordytech.ms_operaciones.repository.BoletaRepository;
import com.cordytech.ms_operaciones.service.ReporteService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RabbitMQConsumerService {

    private final BoletaRepository boletaRepository;
    private final ReporteService reporteService;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public RabbitMQConsumerService(BoletaRepository boletaRepository, ReporteService reporteService) {
        this.boletaRepository = boletaRepository;
        this.reporteService = reporteService;
    }

    @RabbitListener(queues = RabbitMQConfig.REPORTES_QUEUE)
    public void procesarReporte(ReporteRequest reporteRequest) {
        log.info("Procesando solicitud de reporte: {}", reporteRequest);
        
        try {
            // Obtener la boleta desde la base de datos
            BoletaResponse boleta = boletaRepository.findById(reporteRequest.getIdBoleta())
                    .map(this::convertirAResponse)
                    .orElseThrow(() -> new RuntimeException("Boleta no encontrada: " + reporteRequest.getIdBoleta()));
            
            // Generar el reporte según el tipo solicitado
            byte[] reporteBytes;
            String nombreArchivo;
            
            if ("PDF".equalsIgnoreCase(reporteRequest.getTipoReporte())) {
                reporteBytes = reporteService.generarBoletaPDF(boleta).getBody();
                nombreArchivo = "boleta_" + boleta.getIdBoleta() + ".pdf";
            } else if ("EXCEL".equalsIgnoreCase(reporteRequest.getTipoReporte())) {
                reporteBytes = reporteService.generarReporteExcel(java.util.List.of(boleta)).getBody();
                nombreArchivo = "boleta_" + boleta.getIdBoleta() + ".xlsx";
            } else {
                throw new IllegalArgumentException("Tipo de reporte no soportado: " + reporteRequest.getTipoReporte());
            }
            
            // Si se especificó email, enviar el reporte por correo
            if (reporteRequest.getEmailDestinatario() != null && !reporteRequest.getEmailDestinatario().trim().isEmpty()) {
                if (mailSender != null) {
                    enviarReportePorEmail(reporteRequest.getEmailDestinatario(), reporteBytes, nombreArchivo, boleta);
                } else {
                    log.warn("JavaMailSender no configurado, omitiendo envío de email para reporte: {}", nombreArchivo);
                }
            }
            
            log.info("Reporte generado exitosamente: {}", nombreArchivo);
            
        } catch (Exception e) {
            log.error("Error procesando reporte: {}", reporteRequest, e);
            // Aquí podríamos implementar lógica de reintento o dead letter queue
        }
    }

    @RabbitListener(queues = RabbitMQConfig.EMAIL_QUEUE)
    public void procesarEmail(EmailRequest emailRequest) {
        log.info("Procesando solicitud de email: {}", emailRequest);
        
        if (mailSender == null) {
            log.warn("JavaMailSender no configurado, omitiendo envío de email a: {}", emailRequest.getDestinatario());
            return;
        }
        
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            
            helper.setTo(emailRequest.getDestinatario());
            helper.setSubject(emailRequest.getAsunto());
            helper.setText(emailRequest.getCuerpo(), "HTML".equalsIgnoreCase(emailRequest.getTipo()));
            
            // Adjuntar archivos si existen
            if (emailRequest.getAdjuntos() != null) {
                for (java.util.Map.Entry<String, Object> entry : emailRequest.getAdjuntos().entrySet()) {
                    String nombre = entry.getKey();
                    Object contenido = entry.getValue();
                    
                    if (contenido instanceof byte[]) {
                        helper.addAttachment(nombre, new org.springframework.core.io.ByteArrayResource((byte[]) contenido));
                    }
                }
            }
            
            mailSender.send(message);
            log.info("Email enviado exitosamente a: {}", emailRequest.getDestinatario());
            
        } catch (MessagingException e) {
            log.error("Error enviando email: {}", emailRequest, e);
            // Aquí podríamos implementar lógica de reintento
        }
    }

    private void enviarReportePorEmail(String destinatario, byte[] reporteBytes, String nombreArchivo, BoletaResponse boleta) {
        try {
            EmailRequest emailRequest = new EmailRequest();
            emailRequest.setDestinatario(destinatario);
            emailRequest.setAsunto("Reporte de Boleta Portuaria - " + boleta.getIdBoleta());
            emailRequest.setCuerpo(generarCuerpoEmailReporte(boleta));
            emailRequest.setTipo("HTML");
            
            java.util.Map<String, Object> adjuntos = new java.util.HashMap<>();
            adjuntos.put(nombreArchivo, reporteBytes);
            emailRequest.setAdjuntos(adjuntos);
            
            procesarEmail(emailRequest);
            
        } catch (Exception e) {
            log.error("Error enviando reporte por email", e);
        }
    }

    private String generarCuerpoEmailReporte(BoletaResponse boleta) {
        StringBuilder texto = new StringBuilder();
        texto.append("<html><body>");
        texto.append("<h2>Reporte de Boleta Portuaria</h2>");
        texto.append("<p>Estimado/a,</p>");
        texto.append("<p>Se adjunta el reporte solicitado para la boleta:</p>");
        texto.append("<ul>");
        texto.append("<li><strong>ID Boleta:</strong> ").append(boleta.getIdBoleta()).append("</li>");
        texto.append("<li><strong>Fecha Emisión:</strong> ").append(boleta.getFechaEmision()).append("</li>");
        texto.append("<li><strong>Monto:</strong> $").append(boleta.getMonto()).append("</li>");
        texto.append("<li><strong>Estado:</strong> ").append(boleta.getEstado()).append("</li>");
        texto.append("</ul>");
        texto.append("<p>Saludos cordiales,<br>Sistema de Operaciones Portuarias</p>");
        texto.append("</body></html>");
        return texto.toString();
    }

    private BoletaResponse convertirAResponse(com.cordytech.ms_operaciones.model.Boleta boleta) {
        BoletaResponse response = new BoletaResponse();
        response.setIdBoleta(boleta.getIdBoleta());
        response.setFechaEmision(boleta.getFechaEmision());
        response.setMonto(boleta.getMonto());
        response.setEstado(boleta.getEstado().name());
        response.setCodBuque(boleta.getCodBuque());
        response.setIdPuerto(boleta.getIdPuerto());
        response.setIdFuncionario(boleta.getIdFuncionario());
        response.setDiasEstancia(boleta.getDiasEstancia());
        response.setTipoServicio(boleta.getTipoServicio().name());
        response.setObservaciones(boleta.getObservaciones());
        return response;
    }
}
