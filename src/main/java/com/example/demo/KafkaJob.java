package com.example.demo;

import io.opentelemetry.api.trace.Span;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@EnableScheduling
@Configuration
public class KafkaJob {

    @Autowired
    private KafkaListenerEndpointRegistry registry;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String,String> kafkaListenerContainerFactory(ConsumerFactory<String,String> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String,String>();
        factory.setConsumerFactory(consumerFactory);
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setAutoStartup(false);
        return factory;
    }

    @Scheduled(initialDelay = 20_000)
    public void startKafka() {
        var traceId = Span.current().getSpanContext().getTraceId();
        var spanId = Span.current().getSpanContext().getSpanId();
        System.out.println("start kafka");
        System.out.println("spanId:  " + spanId);
        System.out.println("traceId: " + traceId);
        registry.start();
    }

    @Scheduled(initialDelay = 41_000, fixedDelay = 3_000)
    public void send() {
        kafkaTemplate.send("my-topic", "msg " + msgNr);
        msgNr++;
    }
    private int msgNr = 1;

    @KafkaListener(topics = "my-topic", groupId = "my-group-id")
    public void listen(String message) {
        var traceId = Span.current().getSpanContext().getTraceId();
        var spanId = Span.current().getSpanContext().getSpanId();
        System.out.println("message: " + message);
        System.out.println("spanId:  " + spanId);
        System.out.println("traceId: " + traceId);
    }

}
