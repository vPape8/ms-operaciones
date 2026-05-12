package com.cordytech.ms_operaciones.service;

import com.cordytech.ms_operaciones.dto.BoletaRequest;
import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.dto.SimulacionRequest;

import java.util.List;

public interface BoletaService {

    /**
     * Obtiene todas las boletas con sus detalles completos
     */
    List<BoletaResponse> findAll();

    /**
     * Obtiene una boleta por su ID con detalles completos
     */
    BoletaResponse findById(Long id);

    /**
     * Calcula y guarda una nueva boleta en la base de datos
     */
    BoletaResponse calcularYGuardarBoleta(BoletaRequest request);

    /**
     * Simula un cálculo sin guardar en la base de datos
     */
    Double simularCalculo(SimulacionRequest request);

    /**
     * Elimina una boleta por su ID
     */
    void deleteById(Long id);

    /**
     * Obtiene boletas por ID de puerto
     */
    List<BoletaResponse> findByIdPuerto(Long idPuerto);

    /**
     * Obtiene boletas por código de buque
     */
    List<BoletaResponse> findByCodBuque(String codBuque);
}
