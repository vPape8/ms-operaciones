package com.cordytech.ms_operaciones.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cordytech.ms_operaciones.dto.BoletaRequest;
import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.dto.BuqueDTO;
import com.cordytech.ms_operaciones.dto.PuertoDTO;
import com.cordytech.ms_operaciones.dto.SimulacionRequest;
import com.cordytech.ms_operaciones.model.Boleta;
import com.cordytech.ms_operaciones.operaciones.CalculoPortuarioService;
import com.cordytech.ms_operaciones.repository.BoletaRepository;
import com.cordytech.ms_operaciones.service.BoletaService;
import com.cordytech.ms_operaciones.service.ExternalApiService;

@Service
@Transactional
public class BoletaServiceImpl implements BoletaService {

    private final BoletaRepository boletaRepository;
    private final CalculoPortuarioService calculoPortuarioService;
    private final ExternalApiService externalApiService;

    public BoletaServiceImpl(BoletaRepository boletaRepository,
                             CalculoPortuarioService calculoPortuarioService,
                             ExternalApiService externalApiService) {
        this.boletaRepository = boletaRepository;
        this.calculoPortuarioService = calculoPortuarioService;
        this.externalApiService = externalApiService;
    }

    @Override
    public List<BoletaResponse> findAll() {
        return boletaRepository.findAll().stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public BoletaResponse findById(Long id) {
        return boletaRepository.findById(id)
                .map(this::convertirAResponse)
                .orElse(null);
    }

    @Override
    public BoletaResponse calcularYGuardarBoleta(BoletaRequest request) {
        // Obtener datos de APIs externas
        BuqueDTO buque = externalApiService.obtenerBuque(request.getCodBuque());
        PuertoDTO puerto = externalApiService.obtenerPuerto(request.getIdPuerto());

        // Validar funcionario
        if (!externalApiService.validarFuncionario(request.getIdFuncionario())) {
            throw new RuntimeException("Funcionario no válido");
        }

        // Calcular monto
        Double monto = calculoPortuarioService.calcularMontoBoleta(
                buque, puerto, request.getDiasEstancia(), request.getTipoServicio()
        );

        // Crear y guardar boleta
        Boleta boleta = new Boleta();
        boleta.setCodBuque(request.getCodBuque());
        boleta.setIdPuerto(request.getIdPuerto());
        boleta.setIdFuncionario(request.getIdFuncionario());
        boleta.setDiasEstancia(request.getDiasEstancia());
        boleta.setTipoServicio(request.getTipoServicio());
        boleta.setMonto(monto);
        boleta.setObservaciones(request.getObservaciones());
        boleta.setEstado(Boleta.EstadoBoleta.ACTIVA);

        Boleta guardada = boletaRepository.save(boleta);
        return convertirAResponse(guardada);
    }

    @Override
    public Double simularCalculo(SimulacionRequest request) {
        // Obtener datos de APIs externas
        BuqueDTO buque = externalApiService.obtenerBuque(request.getCodBuque());
        PuertoDTO puerto = externalApiService.obtenerPuerto(request.getIdPuerto());

        return calculoPortuarioService.calcularMontoBoleta(
                buque, puerto, request.getDiasEstancia(), request.getTipoServicio()
        );
    }

    @Override
    public void deleteById(Long id) {
        boletaRepository.deleteById(id);
    }

    @Override
    public List<BoletaResponse> findByIdPuerto(Long idPuerto) {
        return boletaRepository.findByIdPuertoOrderByFechaEmisionDesc(idPuerto).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BoletaResponse> findByCodBuque(String codBuque) {
        return boletaRepository.findByCodBuqueOrderByFechaEmisionDesc(codBuque).stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    private BoletaResponse convertirAResponse(Boleta boleta) {
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
        return response;
    }
}
