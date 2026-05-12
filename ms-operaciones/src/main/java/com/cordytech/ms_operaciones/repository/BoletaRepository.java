package com.cordytech.ms_operaciones.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cordytech.ms_operaciones.model.Boleta;

@Repository
public interface BoletaRepository extends JpaRepository<Boleta, Long> {

    List<Boleta> findAllByOrderByFechaEmisionDesc();

    List<Boleta> findByFechaEmisionBetweenOrderByFechaEmisionDesc(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    List<Boleta> findByIdPuertoOrderByFechaEmisionDesc(Long idPuerto);

    List<Boleta> findByCodBuqueOrderByFechaEmisionDesc(String codBuque);
}
