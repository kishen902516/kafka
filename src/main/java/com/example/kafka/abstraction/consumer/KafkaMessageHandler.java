package com.example.kafka.abstraction.consumer;

import org.apache.camel.Exchange;

@FunctionalInterface
public interface KafkaMessageHandler {
    void handle(String topic, String key, String message, Exchange exchange) throws Exception;
}