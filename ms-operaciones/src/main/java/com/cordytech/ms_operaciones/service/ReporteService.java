package com.cordytech.ms_operaciones.service;

import com.cordytech.ms_operaciones.dto.BoletaResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ReporteService {

    /**
     * Genera un reporte PDF con las boletas proporcionadas
     */
    ResponseEntity<byte[]> generarReportePDF(List<BoletaResponse> boletas);

    /**
     * Genera un reporte Excel con las boletas proporcionadas
     */
    ResponseEntity<byte[]> generarReporteExcel(List<BoletaResponse> boletas);

    /**
     * Genera un reporte PDF individual para una boleta específica
     */
    ResponseEntity<byte[]> generarBoletaPDF(BoletaResponse boleta);

    /**
     * Envía una boleta por email con el PDF adjunto
     */
    void enviarBoletaPorEmail(BoletaResponse boleta, String emailDestino);
}
