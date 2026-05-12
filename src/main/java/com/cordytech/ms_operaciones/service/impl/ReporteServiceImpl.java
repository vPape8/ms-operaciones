package com.cordytech.ms_operaciones.service.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.dto.mq.ReporteRequest;
import com.cordytech.ms_operaciones.service.RabbitMQProducerService;
import com.cordytech.ms_operaciones.service.ReporteService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private final RabbitMQProducerService rabbitMQProducerService;

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
        // Enviar solicitud a la cola de RabbitMQ para procesamiento asíncrono
        ReporteRequest reporteRequest = new ReporteRequest();
        reporteRequest.setIdBoleta(boleta.getIdBoleta());
        reporteRequest.setTipoReporte("PDF");
        reporteRequest.setEmailDestinatario(emailDestino);
        reporteRequest.setFechaSolicitud(LocalDateTime.now());
        reporteRequest.setFormato("A4");
        
        rabbitMQProducerService.enviarReporteQueue(reporteRequest);
    }

    private String generarContenidoPDFReporte(List<BoletaResponse> boletas) {
        StringBuilder contenido = new StringBuilder();
        contenido.append("REPORTE DE BOLETAS PORTUARIAS\n\n");
        contenido.append("Fecha de generación: ").append(java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
        BigDecimal totalGeneral = BigDecimal.ZERO;

        for (BoletaResponse boleta : boletas) {
            contenido.append("ID Boleta: ").append(boleta.getIdBoleta()).append("\n");
            contenido.append("Fecha Emisión: ").append(boleta.getFechaEmision().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            contenido.append("Código Buque: ").append(boleta.getCodBuque()).append("\n");
            contenido.append("ID Puerto: ").append(boleta.getIdPuerto()).append("\n");
            contenido.append("Monto: ").append(currencyFormat.format(boleta.getMonto())).append("\n");
            contenido.append("Estado: ").append(boleta.getEstado()).append("\n");
            contenido.append("----------------------------------------\n");
            totalGeneral = totalGeneral.add(boleta.getMonto());
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
}
