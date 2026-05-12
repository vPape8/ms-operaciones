package com.cordytech.ms_operaciones.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReporteRequest {

    private Long idBoleta;
    private String tipoReporte; // PDF, EXCEL
    private String emailDestinatario;
    private LocalDateTime fechaSolicitud;
    private String formato; // A4, LETTER, etc.
}
