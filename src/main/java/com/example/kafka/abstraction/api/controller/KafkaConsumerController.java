package com.example.kafka.abstraction.api.controller;

import com.example.kafka.abstraction.api.dto.ApiResponse;
import com.example.kafka.abstraction.api.dto.ConsumerStatusResponse;
import com.example.kafka.abstraction.api.dto.ConsumerSubscriptionRequest;
import com.example.kafka.abstraction.api.service.ConsumerRegistryService;
import com.example.kafka.abstraction.api.service.WebhookService;
import com.example.kafka.abstraction.consumer.KafkaConsumerException;
import com.example.kafka.abstraction.consumer.KafkaConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/kafka/consumer")
@Validated
public class KafkaConsumerController {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerController.class);

    private final KafkaConsumerService consumerService;
    private final WebhookService webhookService;
    private final ConsumerRegistryService registryService;

    public KafkaConsumerController(KafkaConsumerService consumerService, 
                                  WebhookService webhookService,
                                  ConsumerRegistryService registryService) {
        this.consumerService = consumerService;
        this.webhookService = webhookService;
        this.registryService = registryService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<ApiResponse<ConsumerStatusResponse>> subscribe(@Valid @RequestBody ConsumerSubscriptionRequest request) {
        try {
            logger.info("Received request to subscribe to topic: {} with groupId: {}", request.getTopic(), request.getGroupId());

            if (consumerService.isSubscribed(request.getTopic(), request.getGroupId())) {
                String message = String.format("Already subscribed to topic: %s with groupId: %s", 
                                               request.getTopic(), request.getGroupId());
                ConsumerStatusResponse status = new ConsumerStatusResponse(
                    request.getTopic(), request.getGroupId(), true, "already_subscribed");
                return ResponseEntity.ok(ApiResponse.success(message, status));
            }

            registryService.registerConsumer(request.getTopic(), request.getGroupId(), request.getCallbackUrl());

            consumerService.subscribe(request.getTopic(), request.getGroupId(), (topic, key, message, exchange) -> {
                logger.debug("Received message from topic: {}, key: {}", topic, key);
                String callbackUrl = registryService.getCallbackUrl(topic, request.getGroupId());
                if (callbackUrl != null) {
                    webhookService.sendWebhook(callbackUrl, topic, key, message);
                }
            });

            String responseMessage = String.format("Successfully subscribed to topic: %s with groupId: %s", 
                                                   request.getTopic(), request.getGroupId());
            logger.info(responseMessage);

            ConsumerStatusResponse status = new ConsumerStatusResponse(
                request.getTopic(), request.getGroupId(), true, "subscribed");
            return ResponseEntity.ok(ApiResponse.success(responseMessage, status));

        } catch (KafkaConsumerException e) {
            logger.error("Error subscribing to topic: {} with groupId: {}", request.getTopic(), request.getGroupId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to subscribe: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error subscribing to topic: {} with groupId: {}", request.getTopic(), request.getGroupId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @PostMapping("/subscribe-object")
    public ResponseEntity<ApiResponse<ConsumerStatusResponse>> subscribeForObject(@Valid @RequestBody ConsumerSubscriptionRequest request,
                                                                                  @RequestParam(defaultValue = "java.util.Map") String objectType) {
        try {
            logger.info("Received request to subscribe to object topic: {} with groupId: {} for type: {}", 
                       request.getTopic(), request.getGroupId(), objectType);

            if (consumerService.isSubscribedForObject(request.getTopic(), request.getGroupId())) {
                String message = String.format("Already subscribed to object topic: %s with groupId: %s", 
                                               request.getTopic(), request.getGroupId());
                ConsumerStatusResponse status = new ConsumerStatusResponse(
                    request.getTopic(), request.getGroupId(), true, "already_subscribed_object");
                return ResponseEntity.ok(ApiResponse.success(message, status));
            }

            registryService.registerConsumer(request.getTopic(), request.getGroupId() + "-object", request.getCallbackUrl());

            Class<?> clazz;
            try {
                clazz = Class.forName(objectType);
            } catch (ClassNotFoundException e) {
                clazz = Map.class;
                logger.warn("Object type {} not found, defaulting to Map", objectType);
            }

            consumerService.subscribeForObject(request.getTopic(), request.getGroupId(), clazz, (topic, key, object, exchange) -> {
                logger.debug("Received object from topic: {}, key: {}", topic, key);
                String callbackUrl = registryService.getCallbackUrl(topic, request.getGroupId() + "-object");
                if (callbackUrl != null) {
                    webhookService.sendObjectWebhook(callbackUrl, topic, key, object);
                }
            });

            String responseMessage = String.format("Successfully subscribed to object topic: %s with groupId: %s", 
                                                   request.getTopic(), request.getGroupId());
            logger.info(responseMessage);

            ConsumerStatusResponse status = new ConsumerStatusResponse(
                request.getTopic(), request.getGroupId(), true, "subscribed_object");
            return ResponseEntity.ok(ApiResponse.success(responseMessage, status));

        } catch (KafkaConsumerException e) {
            logger.error("Error subscribing to object topic: {} with groupId: {}", request.getTopic(), request.getGroupId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to subscribe to object topic: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error subscribing to object topic: {} with groupId: {}", request.getTopic(), request.getGroupId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @DeleteMapping("/unsubscribe")
    public ResponseEntity<ApiResponse<ConsumerStatusResponse>> unsubscribe(@RequestParam String topic, 
                                                                          @RequestParam String groupId) {
        try {
            logger.info("Received request to unsubscribe from topic: {} with groupId: {}", topic, groupId);

            if (!consumerService.isSubscribed(topic, groupId)) {
                String message = String.format("Not subscribed to topic: %s with groupId: %s", topic, groupId);
                ConsumerStatusResponse status = new ConsumerStatusResponse(topic, groupId, false, "not_subscribed");
                return ResponseEntity.ok(ApiResponse.success(message, status));
            }

            consumerService.unsubscribe(topic, groupId);
            registryService.unregisterConsumer(topic, groupId);

            String responseMessage = String.format("Successfully unsubscribed from topic: %s with groupId: %s", topic, groupId);
            logger.info(responseMessage);

            ConsumerStatusResponse status = new ConsumerStatusResponse(topic, groupId, false, "unsubscribed");
            return ResponseEntity.ok(ApiResponse.success(responseMessage, status));

        } catch (KafkaConsumerException e) {
            logger.error("Error unsubscribing from topic: {} with groupId: {}", topic, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to unsubscribe: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error unsubscribing from topic: {} with groupId: {}", topic, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @DeleteMapping("/unsubscribe-object")
    public ResponseEntity<ApiResponse<ConsumerStatusResponse>> unsubscribeFromObject(@RequestParam String topic, 
                                                                                    @RequestParam String groupId) {
        try {
            logger.info("Received request to unsubscribe from object topic: {} with groupId: {}", topic, groupId);

            if (!consumerService.isSubscribedForObject(topic, groupId)) {
                String message = String.format("Not subscribed to object topic: %s with groupId: %s", topic, groupId);
                ConsumerStatusResponse status = new ConsumerStatusResponse(topic, groupId, false, "not_subscribed_object");
                return ResponseEntity.ok(ApiResponse.success(message, status));
            }

            consumerService.unsubscribeFromObject(topic, groupId);
            registryService.unregisterConsumer(topic, groupId + "-object");

            String responseMessage = String.format("Successfully unsubscribed from object topic: %s with groupId: %s", topic, groupId);
            logger.info(responseMessage);

            ConsumerStatusResponse status = new ConsumerStatusResponse(topic, groupId, false, "unsubscribed_object");
            return ResponseEntity.ok(ApiResponse.success(responseMessage, status));

        } catch (KafkaConsumerException e) {
            logger.error("Error unsubscribing from object topic: {} with groupId: {}", topic, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to unsubscribe from object topic: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error unsubscribing from object topic: {} with groupId: {}", topic, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Unexpected error occurred"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<ApiResponse<ConsumerStatusResponse>> getConsumerStatus(@RequestParam String topic, 
                                                                                @RequestParam String groupId) {
        try {
            boolean isSubscribed = consumerService.isSubscribed(topic, groupId);
            boolean isSubscribedForObject = consumerService.isSubscribedForObject(topic, groupId);
            
            String status = "not_subscribed";
            if (isSubscribed && isSubscribedForObject) {
                status = "subscribed_both";
            } else if (isSubscribed) {
                status = "subscribed";
            } else if (isSubscribedForObject) {
                status = "subscribed_object";
            }

            ConsumerStatusResponse statusResponse = new ConsumerStatusResponse(
                topic, groupId, isSubscribed || isSubscribedForObject, status);
            
            return ResponseEntity.ok(ApiResponse.success("Consumer status retrieved", statusResponse));

        } catch (Exception e) {
            logger.error("Error getting consumer status for topic: {} with groupId: {}", topic, groupId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to get consumer status"));
        }
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<ConsumerStatusResponse>>> listConsumers() {
        try {
            Map<String, String> allConsumers = registryService.getAllConsumers();
            
            List<ConsumerStatusResponse> consumers = allConsumers.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("-", 2);
                    if (parts.length >= 2) {
                        String topic = parts[0];
                        String groupId = parts[1];
                        boolean isSubscribed = consumerService.isSubscribed(topic, groupId) || 
                                              consumerService.isSubscribedForObject(topic, groupId);
                        return new ConsumerStatusResponse(topic, groupId, isSubscribed, "active");
                    }
                    return null;
                })
                .filter(status -> status != null)
                .collect(Collectors.toList());

            return ResponseEntity.ok(ApiResponse.success("Consumers list retrieved", consumers));

        } catch (Exception e) {
            logger.error("Error listing consumers", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to list consumers"));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<ApiResponse<String>> health() {
        return ResponseEntity.ok(ApiResponse.success("Kafka Consumer API is healthy", "OK"));
    }
}