package com.cordytech.ms_operaciones.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cordytech.ms_operaciones.model.Boleta;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaResponse {

    private Long idBoleta;
    private LocalDateTime fechaEmision;
    private BigDecimal monto;
    private String estado;
    private String codBuque;
    private Long idPuerto;
    private Long idFuncionario;
    private Integer diasEstancia;
    private String tipoServicio;
    private String observaciones;
    private LocalDateTime fechaActualizacion;

    public static BoletaResponse fromEntity(Boleta boleta) {
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
        response.setFechaActualizacion(boleta.getFechaActualizacion());
        return response;
    }
}
