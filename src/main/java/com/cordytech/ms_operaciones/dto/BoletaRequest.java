package com.cordytech.ms_operaciones.dto;

import com.cordytech.ms_operaciones.model.Boleta.TipoServicio;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoletaRequest {

    @NotNull(message = "El código del buque es obligatorio")
    private String codBuque;

    @NotNull(message = "El ID del puerto es obligatorio")
    @Positive(message = "El ID del puerto debe ser positivo")
    private Long idPuerto;

    @NotNull(message = "El ID del funcionario es obligatorio")
    @Positive(message = "El ID del funcionario debe ser positivo")
    private Long idFuncionario;

    @NotNull(message = "Los días de estancia son obligatorios")
    @Positive(message = "Los días de estancia deben ser positivos")
    private Integer diasEstancia;

    @NotNull(message = "El tipo de servicio es obligatorio")
    private TipoServicio tipoServicio;

    private String observaciones;
}
