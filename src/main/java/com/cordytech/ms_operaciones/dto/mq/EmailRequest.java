package com.cordytech.ms_operaciones.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailRequest {

    private String destinatario;
    private String asunto;
    private String cuerpo;
    private Map<String, Object> adjuntos; // nombre -> bytes
    private String tipo; // HTML, TEXT
}
