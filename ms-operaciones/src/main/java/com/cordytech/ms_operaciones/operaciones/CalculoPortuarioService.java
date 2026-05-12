package com.cordytech.ms_operaciones.operaciones;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.cordytech.ms_operaciones.dto.BuqueDTO;
import com.cordytech.ms_operaciones.dto.PuertoDTO;
import com.cordytech.ms_operaciones.dto.SimulacionRequest;
import com.cordytech.ms_operaciones.model.Boleta;

@Service
public class CalculoPortuarioService {

    /**
     * Calcula el monto de una boleta basado en los datos del buque, puerto y días de estancia
     */
    public Double calcularMontoBoleta(BuqueDTO buque, PuertoDTO puerto, Integer diasEstancia, Boleta.TipoServicio tipoServicio) {
        BigDecimal tarifaBase = obtenerTarifaBasePorTipoBuque(buque.getTipo(), puerto);
        BigDecimal tarifaDiaria = tarifaBase.multiply(BigDecimal.valueOf(buque.getEslora() / 10.0));
        
        // Cálculo base: tarifa diaria * días
        BigDecimal montoBase = tarifaDiaria.multiply(BigDecimal.valueOf(diasEstancia));
        
        // Adicionar servicios adicionales
        BigDecimal montoConServicios = aplicarServiciosAdicionales(montoBase, tipoServicio, puerto);
        
        // Adicionar cargo por pasajeros si es crucero
        BigDecimal montoFinal = aplicarCargoPasajeros(montoConServicios, buque);
        
        return montoFinal.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calcula el monto para una simulación sin guardar en BD
     * Nota: Para simulación completa se necesitan los datos del buque y puerto desde las APIs externas
     */
    public Double calcularSimulacion(SimulacionRequest request) {
        // Para simulación, usamos valores por defecto
        // En una implementación completa, se obtendrían los datos reales de las APIs externas
        
        // Usar tarifas por defecto para simulación
        BigDecimal tarifaBase = BigDecimal.valueOf(1000.0); // Tarifa base por defecto
        BigDecimal eslora = BigDecimal.valueOf(150.0); // Eslora por defecto
        
        BigDecimal tarifaDiaria = tarifaBase.multiply(eslora.divide(BigDecimal.valueOf(10.0)));
        
        // Cálculo base
        BigDecimal montoBase = tarifaDiaria.multiply(BigDecimal.valueOf(request.getDiasEstancia()));
        
        // Adicionar servicios adicionales
        BigDecimal montoConServicios = aplicarServiciosAdicionalesSimulacion(montoBase, request.getTipoServicio());
        
        return montoConServicios.setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    private BigDecimal obtenerTarifaBasePorTipoBuque(BuqueDTO.TipoBuque tipoBuque, PuertoDTO puerto) {
        return switch (tipoBuque) {
            case GENERAL -> puerto.getTarifaBaseGeneral() != null ? puerto.getTarifaBaseGeneral() : BigDecimal.valueOf(1000.0);
            case PESQUERO -> puerto.getTarifaBasePesquero() != null ? puerto.getTarifaBasePesquero() : BigDecimal.valueOf(800.0);
            case MILITAR -> puerto.getTarifaBaseMilitar() != null ? puerto.getTarifaBaseMilitar() : BigDecimal.valueOf(500.0);
            case INVESTIGACION -> puerto.getTarifaBaseInvestigacion() != null ? puerto.getTarifaBaseInvestigacion() : BigDecimal.valueOf(600.0);
            case CRUCERO -> puerto.getTarifaBaseCrucero() != null ? puerto.getTarifaBaseCrucero() : BigDecimal.valueOf(1500.0);
        };
    }

    private BigDecimal aplicarServiciosAdicionales(BigDecimal montoBase, Boleta.TipoServicio tipoServicio, PuertoDTO puerto) {
        return switch (tipoServicio) {
            case BASICO -> montoBase;
            case MEDIO -> {
                BigDecimal cargoRemolque = puerto.getTarifaRemolque() != null ? puerto.getTarifaRemolque() : BigDecimal.valueOf(500.0);
                yield montoBase.add(cargoRemolque);
            }
            case COMPLETO -> {
                BigDecimal cargoRemolque = puerto.getTarifaRemolque() != null ? puerto.getTarifaRemolque() : BigDecimal.valueOf(500.0);
                BigDecimal cargoSuministros = puerto.getTarifaSuministros() != null ? puerto.getTarifaSuministros() : BigDecimal.valueOf(800.0);
                yield montoBase.add(cargoRemolque).add(cargoSuministros);
            }
        };
    }

    private BigDecimal aplicarServiciosAdicionalesSimulacion(BigDecimal montoBase, Boleta.TipoServicio tipoServicio) {
        return switch (tipoServicio) {
            case BASICO -> montoBase;
            case MEDIO -> montoBase.add(BigDecimal.valueOf(500.0)); // Cargo remolque
            case COMPLETO -> montoBase.add(BigDecimal.valueOf(500.0)).add(BigDecimal.valueOf(800.0)); // Remolque + suministros
        };
    }

    private BigDecimal aplicarCargoPasajeros(BigDecimal monto, BuqueDTO buque) {
        if (buque.getTipo() == BuqueDTO.TipoBuque.CRUCERO && buque.getCapacidadPasajeros() != null) {
            // Usar tarifa por defecto
            BigDecimal tarifaPasajero = BigDecimal.valueOf(50.0); // $50 por pasajero (tarifa por defecto)
            return monto.add(tarifaPasajero.multiply(BigDecimal.valueOf(buque.getCapacidadPasajeros())));
        }
        return monto;
    }
}
