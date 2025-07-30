package com.example.kafka.abstraction.api.controller;

import com.example.kafka.abstraction.api.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiDocumentationController {

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Kafka Abstraction API is healthy", "OK"));
    }

    @GetMapping("/info")
    public ResponseEntity<ApiResponse<Map<String, Object>>> info() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "Kafka Abstraction API");
        info.put("version", "1.0.0");
        info.put("description", "REST API for Kafka producer and consumer operations with SSL certificate support");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("POST /api/kafka/producer/send", "Send a string message to Kafka topic");
        endpoints.put("POST /api/kafka/producer/send-async", "Send a string message asynchronously");
        endpoints.put("POST /api/kafka/producer/send-object", "Send an object to Kafka topic");
        endpoints.put("POST /api/kafka/producer/send-object-async", "Send an object asynchronously");
        endpoints.put("POST /api/kafka/consumer/subscribe", "Subscribe to a Kafka topic with webhook callback");
        endpoints.put("POST /api/kafka/consumer/subscribe-object", "Subscribe to object messages with webhook callback");
        endpoints.put("DELETE /api/kafka/consumer/unsubscribe", "Unsubscribe from a Kafka topic");
        endpoints.put("DELETE /api/kafka/consumer/unsubscribe-object", "Unsubscribe from object messages");
        endpoints.put("GET /api/kafka/consumer/status", "Get consumer subscription status");
        endpoints.put("GET /api/kafka/consumer/list", "List all active consumers");
        endpoints.put("GET /api/health", "API health check");
        endpoints.put("GET /api/info", "API information and endpoints");
        
        info.put("endpoints", endpoints);
        
        Map<String, String> features = new HashMap<>();
        features.put("SSL/TLS Support", "Certificate-based authentication for Confluent Kafka");
        features.put("Async Operations", "Non-blocking message sending capabilities");
        features.put("Webhook Integration", "HTTP callbacks for consumed messages");
        features.put("Object Support", "JSON serialization/deserialization");
        features.put("Error Handling", "Comprehensive error responses");
        features.put("Auto Configuration", "Spring Boot auto-configuration");
        
        info.put("features", features);
        
        return ResponseEntity.ok(ApiResponse.success("API information retrieved", info));
    }

    @GetMapping("/examples")
    public ResponseEntity<ApiResponse<Map<String, Object>>> examples() {
        Map<String, Object> examples = new HashMap<>();
        
        Map<String, Object> producerExamples = new HashMap<>();
        
        Map<String, Object> sendMessage = new HashMap<>();
        sendMessage.put("url", "POST /api/kafka/producer/send");
        sendMessage.put("body", Map.of(
            "topic", "my-topic",
            "key", "user-123",
            "message", "Hello Kafka!"
        ));
        producerExamples.put("sendMessage", sendMessage);
        
        Map<String, Object> sendObject = new HashMap<>();
        sendObject.put("url", "POST /api/kafka/producer/send-object");
        sendObject.put("body", Map.of(
            "topic", "user-events",
            "key", "user-456",
            "data", Map.of(
                "id", "456",
                "name", "John Doe",
                "email", "john@example.com"
            )
        ));
        producerExamples.put("sendObject", sendObject);
        
        examples.put("producer", producerExamples);
        
        Map<String, Object> consumerExamples = new HashMap<>();
        
        Map<String, Object> subscribe = new HashMap<>();
        subscribe.put("url", "POST /api/kafka/consumer/subscribe");
        subscribe.put("body", Map.of(
            "topic", "my-topic",
            "groupId", "my-consumer-group",
            "callbackUrl", "https://your-app.com/webhook/kafka"
        ));
        consumerExamples.put("subscribe", subscribe);
        
        Map<String, Object> webhookPayload = new HashMap<>();
        webhookPayload.put("description", "Webhook payload sent to your callback URL");
        webhookPayload.put("payload", Map.of(
            "topic", "my-topic",
            "key", "user-123",
            "message", "Hello Kafka!",
            "timestamp", 1640995200000L
        ));
        consumerExamples.put("webhookPayload", webhookPayload);
        
        examples.put("consumer", consumerExamples);
        
        return ResponseEntity.ok(ApiResponse.success("API examples retrieved", examples));
    }
}