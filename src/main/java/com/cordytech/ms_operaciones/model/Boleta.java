package com.cordytech.ms_operaciones.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boletas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Boleta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_boleta")
    private Long idBoleta;

    @Column(name = "fecha_emision", nullable = false)
    @CreatedDate
    private LocalDateTime fechaEmision;

    @Column(name = "monto", nullable = false, precision = 19, scale = 2)
    private BigDecimal monto;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoBoleta estado = EstadoBoleta.ACTIVA;

    // Almacenar IDs de entidades externas en lugar de referencias JPA
    @Column(name = "cod_buque", nullable = false)
    private String codBuque;

    @Column(name = "id_puerto", nullable = false)
    private Long idPuerto;

    @Column(name = "id_funcionario", nullable = false)
    private Long idFuncionario;

    @Column(name = "dias_estancia", nullable = false)
    private Integer diasEstancia;

    @Column(name = "tipo_servicio", nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoServicio tipoServicio;

    @Column(name = "observaciones")
    private String observaciones;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    public enum EstadoBoleta {
        ACTIVA, CANCELADA, PAGADA
    }

    public enum TipoServicio {
        BASICO, MEDIO, COMPLETO
    }
}
