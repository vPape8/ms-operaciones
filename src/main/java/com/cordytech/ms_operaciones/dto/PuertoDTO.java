package com.cordytech.ms_operaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuertoDTO {
    private Long idPuerto;
    private String nombre;
    private String pais;
    private String ciudad;
    private BigDecimal tarifaBaseGeneral;
    private BigDecimal tarifaBasePesquero;
    private BigDecimal tarifaBaseMilitar;
    private BigDecimal tarifaBaseInvestigacion;
    private BigDecimal tarifaBaseCrucero;
    private BigDecimal tarifaRemolque;
    private BigDecimal tarifaSuministros;
    private BigDecimal tarifaPasajero;
}
