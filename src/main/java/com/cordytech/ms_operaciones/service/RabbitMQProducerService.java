package com.cordytech.ms_operaciones.service;

import com.cordytech.ms_operaciones.config.RabbitMQConfig;
import com.cordytech.ms_operaciones.dto.mq.EmailRequest;
import com.cordytech.ms_operaciones.dto.mq.ReporteRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class RabbitMQProducerService {

    private final RabbitTemplate rabbitTemplate;

    public RabbitMQProducerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void enviarReporteQueue(ReporteRequest reporteRequest) {
        log.info("Enviando solicitud de reporte a la cola: {}", reporteRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.REPORTES_EXCHANGE,
                RabbitMQConfig.REPORTES_ROUTING_KEY,
                reporteRequest
        );
    }

    public void enviarEmailQueue(EmailRequest emailRequest) {
        log.info("Enviando solicitud de email a la cola: {}", emailRequest);
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EMAIL_EXCHANGE,
                RabbitMQConfig.EMAIL_ROUTING_KEY,
                emailRequest
        );
    }
}
