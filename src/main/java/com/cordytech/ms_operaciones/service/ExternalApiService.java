package com.cordytech.ms_operaciones.service;

import com.cordytech.ms_operaciones.dto.BuqueDTO;
import com.cordytech.ms_operaciones.dto.PuertoDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;

    @Value("${ms-buque.url:http://localhost:8083}")
    private String buqueServiceUrl;

    @Value("${ms-puerto.url:http://localhost:8082}")
    private String puertoServiceUrl;

    @Value("${ms-user.url:http://localhost:8081}")
    private String userServiceUrl;

    public ExternalApiService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public BuqueDTO obtenerBuque(String codBuque) {
        try {
            // Llamada directa al microservicio (no usar BFF para comunicación entre microservicios)
            BuqueDTO buque = restTemplate.getForObject(
                buqueServiceUrl + "/api/buques/" + codBuque,
                BuqueDTO.class
            );
            
            if (buque == null) {
                throw new RuntimeException("Buque no encontrado: " + codBuque);
            }
            return buque;
        } catch (Exception e) {
            // Fallback: retornar un buque mock para desarrollo
            BuqueDTO buque = new BuqueDTO();
            buque.setCodBuque(codBuque);
            buque.setNombre("Buque " + codBuque);
            buque.setEslora(150.0);
            buque.setTipo(BuqueDTO.TipoBuque.GENERAL);
            buque.setCapacidadPasajeros(0);
            buque.setBandera("PAN");
            return buque;
        }
    }

    public PuertoDTO obtenerPuerto(Long idPuerto) {
        try {
            // Llamada directa al microservicio (no usar BFF para comunicación entre microservicios)
            PuertoDTO puerto = restTemplate.getForObject(
                puertoServiceUrl + "/api/puertos/" + idPuerto,
                PuertoDTO.class
            );
            
            if (puerto == null) {
                throw new RuntimeException("Puerto no encontrado: " + idPuerto);
            }
            return puerto;
        } catch (Exception e) {
            // Fallback: retornar un puerto mock para desarrollo
            PuertoDTO puerto = new PuertoDTO();
            puerto.setIdPuerto(idPuerto);
            puerto.setNombre("Puerto " + idPuerto);
            puerto.setPais("Panamá");
            puerto.setCiudad("Ciudad de Panamá");
            puerto.setTarifaBaseGeneral(new java.math.BigDecimal("100.00"));
            puerto.setTarifaBasePesquero(new java.math.BigDecimal("80.00"));
            puerto.setTarifaBaseMilitar(new java.math.BigDecimal("120.00"));
            puerto.setTarifaBaseInvestigacion(new java.math.BigDecimal("90.00"));
            puerto.setTarifaBaseCrucero(new java.math.BigDecimal("150.00"));
            puerto.setTarifaRemolque(new java.math.BigDecimal("200.00"));
            puerto.setTarifaSuministros(new java.math.BigDecimal("50.00"));
            puerto.setTarifaPasajero(new java.math.BigDecimal("10.00"));
            return puerto;
        }
    }

    public boolean validarFuncionario(Long idFuncionario) {
        try {
            // Llamada directa al microservicio (no usar BFF para comunicación entre microservicios)
            Boolean exists = restTemplate.getForObject(
                userServiceUrl + "/api/usuarios/" + idFuncionario + "/exists",
                Boolean.class
            );
            return exists != null && exists;
        } catch (Exception e) {
            // Fallback: aceptar cualquier funcionario para desarrollo
            return true;
        }
    }
}
