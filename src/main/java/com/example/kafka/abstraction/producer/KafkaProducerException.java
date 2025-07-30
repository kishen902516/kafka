package com.example.kafka.abstraction.producer;

public class KafkaProducerException extends RuntimeException {

    public KafkaProducerException(String message) {
        super(message);
    }

    public KafkaProducerException(String message, Throwable cause) {
        super(message, cause);
    }

    public KafkaProducerException(Throwable cause) {
        super(cause);
    }
}