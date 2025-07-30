package com.example.kafka.abstraction.api.controller;

import com.example.kafka.abstraction.api.dto.ApiResponse;
import com.example.kafka.abstraction.api.dto.MessageRequest;
import com.example.kafka.abstraction.api.dto.ObjectMessageRequest;
import com.example.kafka.abstraction.producer.KafkaProducerException;
import com.example.kafka.abstraction.producer.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/kafka/producer")
@Validated
public class KafkaProducerController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerController.class);

    private final KafkaProducerService producerService;

    public KafkaProducerController(KafkaProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/send")
    public ResponseEntity<ApiResponse<String>> sendMessage(@Valid @RequestBody MessageRequest request) {
        try {
            logger.info("Received request to send message to topic: {}", request.getTopic());
            
            producerService.send(request.getTopic(), request.getKey(), request.getMessage());
            
            String responseMessage = String.format("Message sent successfully to topic: %s", request.getTopic());
            logger.info(responseMessage);
            
            return ResponseEntity.ok(ApiResponse.success(responseMessage, "Message delivered"));
            
        } catch (KafkaProducerException e) {
            logger.error("Error sending message to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send message: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error sending message to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @PostMapping("/send-async")
    public ResponseEntity<ApiResponse<String>> sendMessageAsync(@Valid @RequestBody MessageRequest request) {
        try {
            logger.info("Received request to send async message to topic: {}", request.getTopic());
            
            CompletableFuture<Void> future = producerService.sendAsync(request.getTopic(), request.getKey(), request.getMessage());
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Error in async message delivery to topic: {}", request.getTopic(), throwable);
                } else {
                    logger.info("Async message delivered successfully to topic: {}", request.getTopic());
                }
            });
            
            String responseMessage = String.format("Message queued for async delivery to topic: %s", request.getTopic());
            return ResponseEntity.accepted().body(ApiResponse.success(responseMessage, "Message queued"));
            
        } catch (Exception e) {
            logger.error("Error queuing async message to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to queue message for async delivery: " + e.getMessage()));
        }
    }

    @PostMapping("/send-object")
    public ResponseEntity<ApiResponse<String>> sendObject(@Valid @RequestBody ObjectMessageRequest request) {
        try {
            logger.info("Received request to send object to topic: {}", request.getTopic());
            
            producerService.sendObject(request.getTopic(), request.getKey(), request.getData());
            
            String responseMessage = String.format("Object sent successfully to topic: %s", request.getTopic());
            logger.info(responseMessage);
            
            return ResponseEntity.ok(ApiResponse.success(responseMessage, "Object delivered"));
            
        } catch (KafkaProducerException e) {
            logger.error("Error sending object to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to send object: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error sending object to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @PostMapping("/send-object-async")
    public ResponseEntity<ApiResponse<String>> sendObjectAsync(@Valid @RequestBody ObjectMessageRequest request) {
        try {
            logger.info("Received request to send async object to topic: {}", request.getTopic());
            
            CompletableFuture<Void> future = producerService.sendObjectAsync(request.getTopic(), request.getKey(), request.getData());
            
            future.whenComplete((result, throwable) -> {
                if (throwable != null) {
                    logger.error("Error in async object delivery to topic: {}", request.getTopic(), throwable);
                } else {
                    logger.info("Async object delivered successfully to topic: {}", request.getTopic());
                }
            });
            
            String responseMessage = String.format("Object queued for async delivery to topic: %s", request.getTopic());
            return ResponseEntity.accepted().body(ApiResponse.success(responseMessage, "Object queued"));
            
        } catch (Exception e) {
            logger.error("Error queuing async object to topic: {}", request.getTopic(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to queue object for async delivery: " + e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Kafka Producer API is healthy", "OK"));
    }
}