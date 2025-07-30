package com.example.kafka.abstraction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private final CamelContext camelContext;
    private final ObjectMapper objectMapper;
    private final ConcurrentMap<String, KafkaConsumerRoute> activeRoutes = new ConcurrentHashMap<>();
    private final ConcurrentMap<String, KafkaObjectConsumerRoute<?>> activeObjectRoutes = new ConcurrentHashMap<>();

    public KafkaConsumerService(CamelContext camelContext, ObjectMapper objectMapper) {
        this.camelContext = camelContext;
        this.objectMapper = objectMapper;
    }

    public void subscribe(String topic, String groupId, KafkaMessageHandler messageHandler) {
        String routeKey = topic + "-" + groupId;
        
        if (activeRoutes.containsKey(routeKey)) {
            logger.warn("Consumer route already exists for topic: {} and groupId: {}", topic, groupId);
            return;
        }

        try {
            KafkaConsumerRoute route = new KafkaConsumerRoute(topic, groupId, messageHandler);
            camelContext.addRoutes(route);
            activeRoutes.put(routeKey, route);
            logger.info("Successfully subscribed to topic: {} with groupId: {}", topic, groupId);
        } catch (Exception e) {
            logger.error("Error subscribing to topic: {} with groupId: {}", topic, groupId, e);
            throw new KafkaConsumerException("Failed to subscribe to topic: " + topic, e);
        }
    }

    public <T> void subscribeForObject(String topic, String groupId, Class<T> objectType, 
                                       KafkaObjectHandler<T> objectHandler) {
        String routeKey = topic + "-" + groupId + "-object";
        
        if (activeObjectRoutes.containsKey(routeKey)) {
            logger.warn("Object consumer route already exists for topic: {} and groupId: {}", topic, groupId);
            return;
        }

        try {
            KafkaObjectConsumerRoute<T> route = new KafkaObjectConsumerRoute<>(
                topic, groupId, objectHandler, objectType, objectMapper);
            camelContext.addRoutes(route);
            activeObjectRoutes.put(routeKey, route);
            logger.info("Successfully subscribed to topic: {} with groupId: {} for object type: {}", 
                       topic, groupId, objectType.getSimpleName());
        } catch (Exception e) {
            logger.error("Error subscribing to topic: {} with groupId: {} for object type: {}", 
                        topic, groupId, objectType.getSimpleName(), e);
            throw new KafkaConsumerException("Failed to subscribe to topic: " + topic, e);
        }
    }

    public void unsubscribe(String topic, String groupId) {
        String routeKey = topic + "-" + groupId;
        String routeId = "kafka-consumer-" + topic + "-" + groupId;
        
        try {
            camelContext.getRouteController().stopRoute(routeId);
            camelContext.removeRoute(routeId);
            activeRoutes.remove(routeKey);
            logger.info("Successfully unsubscribed from topic: {} with groupId: {}", topic, groupId);
        } catch (Exception e) {
            logger.error("Error unsubscribing from topic: {} with groupId: {}", topic, groupId, e);
            throw new KafkaConsumerException("Failed to unsubscribe from topic: " + topic, e);
        }
    }

    public void unsubscribeFromObject(String topic, String groupId) {
        String routeKey = topic + "-" + groupId + "-object";
        String routeId = "kafka-object-consumer-" + topic + "-" + groupId;
        
        try {
            camelContext.getRouteController().stopRoute(routeId);
            camelContext.removeRoute(routeId);
            activeObjectRoutes.remove(routeKey);
            logger.info("Successfully unsubscribed from object topic: {} with groupId: {}", topic, groupId);
        } catch (Exception e) {
            logger.error("Error unsubscribing from object topic: {} with groupId: {}", topic, groupId, e);
            throw new KafkaConsumerException("Failed to unsubscribe from object topic: " + topic, e);
        }
    }

    public boolean isSubscribed(String topic, String groupId) {
        String routeKey = topic + "-" + groupId;
        return activeRoutes.containsKey(routeKey);
    }

    public boolean isSubscribedForObject(String topic, String groupId) {
        String routeKey = topic + "-" + groupId + "-object";
        return activeObjectRoutes.containsKey(routeKey);
    }
}