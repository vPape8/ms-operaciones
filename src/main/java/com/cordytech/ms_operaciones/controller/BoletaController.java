package com.cordytech.ms_operaciones.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cordytech.ms_operaciones.dto.BoletaRequest;
import com.cordytech.ms_operaciones.dto.BoletaResponse;
import com.cordytech.ms_operaciones.dto.SimulacionRequest;
import com.cordytech.ms_operaciones.service.BoletaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/boletas")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class BoletaController {

    private final BoletaService boletaService;

    @GetMapping
    public ResponseEntity<List<EntityModel<BoletaResponse>>> findAll() {
        List<BoletaResponse> boletas = boletaService.findAll();
        List<EntityModel<BoletaResponse>> boletasWithLinks = boletas.stream()
                .map(this::addLinksToBoleta)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(boletasWithLinks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<BoletaResponse>> findById(@PathVariable Long id) {
        BoletaResponse boleta = boletaService.findById(id);
        EntityModel<BoletaResponse> boletaWithLinks = addLinksToBoleta(boleta);
        return ResponseEntity.ok(boletaWithLinks);
    }

    @GetMapping("/boleta/{id}")
    public ResponseEntity<EntityModel<BoletaResponse>> getBoleta(@PathVariable Long id) {
        // Endpoint adicional para compatibilidad con frontend
        return findById(id);
    }

    @PostMapping("/calcular")
    public ResponseEntity<EntityModel<BoletaResponse>> calcularYGuardar(@Valid @RequestBody BoletaRequest request) {
        BoletaResponse boletaCreada = boletaService.calcularYGuardarBoleta(request);
        EntityModel<BoletaResponse> boletaWithLinks = addLinksToBoleta(boletaCreada);
        return ResponseEntity.ok(boletaWithLinks);
    }

    @PostMapping("/simular")
    public ResponseEntity<BigDecimal> simular(@Valid @RequestBody SimulacionRequest request) {
        BigDecimal resultado = boletaService.simularCalculo(request);
        return ResponseEntity.ok(resultado);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        boletaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/puerto/{idPuerto}")
    public ResponseEntity<List<EntityModel<BoletaResponse>>> findByPuerto(@PathVariable Long idPuerto) {
        List<BoletaResponse> boletas = boletaService.findByIdPuerto(idPuerto);
        List<EntityModel<BoletaResponse>> boletasWithLinks = boletas.stream()
                .map(this::addLinksToBoleta)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(boletasWithLinks);
    }

    @GetMapping("/buque/{codBuque}")
    public ResponseEntity<List<EntityModel<BoletaResponse>>> findByBuque(@PathVariable String codBuque) {
        List<BoletaResponse> boletas = boletaService.findByCodBuque(codBuque);
        List<EntityModel<BoletaResponse>> boletasWithLinks = boletas.stream()
                .map(this::addLinksToBoleta)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(boletasWithLinks);
    }

    private EntityModel<BoletaResponse> addLinksToBoleta(BoletaResponse boleta) {
        EntityModel<BoletaResponse> boletaModel = EntityModel.of(boleta);
        
        // Self link
        Link selfLink = linkTo(methodOn(BoletaController.class).findById(boleta.getIdBoleta())).withSelfRel();
        boletaModel.add(selfLink);
        
        // Collection link
        Link collectionLink = linkTo(methodOn(BoletaController.class).findAll()).withRel("boletas");
        boletaModel.add(collectionLink);
        
        // Simulación link
        Link simulacionLink = linkTo(methodOn(BoletaController.class).simular(new SimulacionRequest())).withRel("simular");
        boletaModel.add(simulacionLink);
        
        return boletaModel;
    }
}
