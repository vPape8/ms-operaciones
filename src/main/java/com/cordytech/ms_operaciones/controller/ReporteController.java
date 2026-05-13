package com.cordytech.ms_operaciones.controller;

import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.service.BoletaService;
import com.cordytech.ms_operaciones.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;
    private final BoletaService boletaService;

    @PostMapping("/pdf")
    public ResponseEntity<byte[]> generarReportePDF(@RequestBody List<Long> idsBoletas) {
        List<BoletaResponse> boletas = idsBoletas.stream()
                .map(boletaService::findById)
                .toList();
        
        return reporteService.generarReportePDF(boletas);
    }

    @PostMapping("/excel")
    public ResponseEntity<byte[]> generarReporteExcel(@RequestBody List<Long> idsBoletas) {
        List<BoletaResponse> boletas = idsBoletas.stream()
                .map(boletaService::findById)
                .toList();
        
        return reporteService.generarReporteExcel(boletas);
    }

    @GetMapping("/boleta/{id}/pdf")
    public ResponseEntity<byte[]> generarBoletaPDF(@PathVariable Long id) {
        BoletaResponse boleta = boletaService.findById(id);
        return reporteService.generarBoletaPDF(boleta);
    }

    @PostMapping("/boleta/{id}/email")
    public ResponseEntity<String> enviarBoletaPorEmail(@PathVariable Long id, @RequestParam String email) {
        BoletaResponse boleta = boletaService.findById(id);
        reporteService.enviarBoletaPorEmail(boleta, email);
        return ResponseEntity.ok("Boleta enviada exitosamente a " + email);
    }

    @PostMapping("/todos/pdf")
    public ResponseEntity<byte[]> generarReporteCompletoPDF() {
        List<BoletaResponse> todasLasBoletas = boletaService.findAll();
        return reporteService.generarReportePDF(todasLasBoletas);
    }

    @PostMapping("/todos/excel")
    public ResponseEntity<byte[]> generarReporteCompletoExcel() {
        List<BoletaResponse> todasLasBoletas = boletaService.findAll();
        return reporteService.generarReporteExcel(todasLasBoletas);
    }
}
