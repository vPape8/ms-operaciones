package com.cordytech.ms_operaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuqueDTO {
    private String codBuque;
    private String nombre;
    private Double eslora;
    private TipoBuque tipo;
    private Integer capacidadPasajeros;
    private String bandera;

    public enum TipoBuque {
        GENERAL, PESQUERO, MILITAR, INVESTIGACION, CRUCERO
    }
}
