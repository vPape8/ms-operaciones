package com.cordytech.ms_operaciones.dto;

import com.cordytech.ms_operaciones.model.Boleta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimulacionRequest {

    @NotNull(message = "El código de buque es obligatorio")
    private String codBuque;

    @NotNull(message = "El ID de puerto es obligatorio")
    private Long idPuerto;

    @NotNull(message = "Los días de estancia son obligatorios")
    @Positive(message = "Los días deben ser positivos")
    private Integer diasEstancia;

    @NotNull(message = "El tipo de servicio es obligatorio")
    private Boleta.TipoServicio tipoServicio;
}
