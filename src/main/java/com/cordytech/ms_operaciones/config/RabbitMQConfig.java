package com.cordytech.ms_operaciones.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String REPORTES_QUEUE = "reportes.queue";
    public static final String EMAIL_QUEUE = "email.queue";
    public static final String REPORTES_EXCHANGE = "reportes.exchange";
    public static final String EMAIL_EXCHANGE = "email.exchange";
    public static final String REPORTES_ROUTING_KEY = "reporte.generar";
    public static final String EMAIL_ROUTING_KEY = "email.enviar";

    @Bean
    public Queue reportesQueue() {
        return new Queue(REPORTES_QUEUE, true);
    }

    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true);
    }

    @Bean
    public TopicExchange reportesExchange() {
        return new TopicExchange(REPORTES_EXCHANGE);
    }

    @Bean
    public TopicExchange emailExchange() {
        return new TopicExchange(EMAIL_EXCHANGE);
    }

    @Bean
    public Binding reportesBinding() {
        return BindingBuilder
                .bind(reportesQueue())
                .to(reportesExchange())
                .with(REPORTES_ROUTING_KEY);
    }

    @Bean
    public Binding emailBinding() {
        return BindingBuilder
                .bind(emailQueue())
                .to(emailExchange())
                .with(EMAIL_ROUTING_KEY);
    }
}
