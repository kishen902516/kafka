package com.example.kafka.abstraction.consumer;

public class KafkaConsumerException extends RuntimeException {

    public KafkaConsumerException(String message) {
        super(message);
    }

    public KafkaConsumerException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaConsumerException(Throwable cause) {
        super(cause);
    }
}