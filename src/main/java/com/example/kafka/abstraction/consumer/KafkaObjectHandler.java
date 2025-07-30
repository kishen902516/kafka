package com.example.kafka.abstraction.consumer;

import org.apache.camel.Exchange;

@FunctionalInterface
public interface KafkaObjectHandler<T> {
    void handle(String topic, String key, T object, Exchange exchange) throws Exception;
}