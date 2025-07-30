package com.example.kafka.abstraction.producer;

import org.apache.camel.ProducerTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    
    private final ProducerTemplate producerTemplate;

    public KafkaProducerService(ProducerTemplate producerTemplate) {
        this.producerTemplate = producerTemplate;
    }

    public void send(String topic, String message) {
        send(topic, null, message);
    }

    public void send(String topic, String key, String message) {
        try {
            String kafkaEndpoint = buildKafkaEndpoint(topic);
            
            if (key != null) {
                producerTemplate.sendBodyAndHeader(kafkaEndpoint, message, "kafka.KEY", key);
            } else {
                producerTemplate.sendBody(kafkaEndpoint, message);
            }
            
            logger.debug("Successfully sent message to topic: {}", topic);
        } catch (Exception e) {
            logger.error("Error sending message to topic: {}", topic, e);
            throw new KafkaProducerException("Failed to send message to topic: " + topic, e);
        }
    }

    public void send(String topic, String key, String message, Map<String, Object> headers) {
        try {
            String kafkaEndpoint = buildKafkaEndpoint(topic);
            
            if (key != null) {
                headers.put("kafka.KEY", key);
            }
            
            producerTemplate.sendBodyAndHeaders(kafkaEndpoint, message, headers);
            logger.debug("Successfully sent message with headers to topic: {}", topic);
        } catch (Exception e) {
            logger.error("Error sending message with headers to topic: {}", topic, e);
            throw new KafkaProducerException("Failed to send message with headers to topic: " + topic, e);
        }
    }

    public CompletableFuture<Void> sendAsync(String topic, String message) {
        return sendAsync(topic, null, message);
    }

    public CompletableFuture<Void> sendAsync(String topic, String key, String message) {
        return CompletableFuture.runAsync(() -> send(topic, key, message));
    }

    public CompletableFuture<Void> sendAsync(String topic, String key, String message, Map<String, Object> headers) {
        return CompletableFuture.runAsync(() -> send(topic, key, message, headers));
    }

    public <T> void sendObject(String topic, T object) {
        sendObject(topic, null, object);
    }

    public <T> void sendObject(String topic, String key, T object) {
        try {
            String kafkaEndpoint = buildKafkaEndpoint(topic);
            
            if (key != null) {
                producerTemplate.sendBodyAndHeader(kafkaEndpoint, object, "kafka.KEY", key);
            } else {
                producerTemplate.sendBody(kafkaEndpoint, object);
            }
            
            logger.debug("Successfully sent object to topic: {}", topic);
        } catch (Exception e) {
            logger.error("Error sending object to topic: {}", topic, e);
            throw new KafkaProducerException("Failed to send object to topic: " + topic, e);
        }
    }

    public <T> CompletableFuture<Void> sendObjectAsync(String topic, T object) {
        return sendObjectAsync(topic, null, object);
    }

    public <T> CompletableFuture<Void> sendObjectAsync(String topic, String key, T object) {
        return CompletableFuture.runAsync(() -> sendObject(topic, key, object));
    }

    private String buildKafkaEndpoint(String topic) {
        return "kafka:" + topic;
    }
}