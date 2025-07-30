package com.example.kafka.abstraction.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.ResourceAccessException;

import java.util.HashMap;
import java.util.Map;

@Service
public class WebhookService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);

    private final RestTemplate restTemplate;

    public WebhookService() {
        this.restTemplate = new RestTemplate();
    }

    public void sendWebhook(String callbackUrl, String topic, String key, String message) {
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            logger.debug("No callback URL provided, skipping webhook for topic: {}", topic);
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("topic", topic);
            payload.put("key", key);
            payload.put("message", message);
            payload.put("timestamp", System.currentTimeMillis());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            logger.debug("Sending webhook to: {} for topic: {}", callbackUrl, topic);

            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Webhook sent successfully to: {} for topic: {}", callbackUrl, topic);
            } else {
                logger.warn("Webhook failed with status: {} for URL: {} topic: {}", 
                           response.getStatusCode(), callbackUrl, topic);
            }

        } catch (ResourceAccessException e) {
            logger.error("Network error sending webhook to: {} for topic: {}", callbackUrl, topic, e);
        } catch (Exception e) {
            logger.error("Error sending webhook to: {} for topic: {}", callbackUrl, topic, e);
        }
    }

    public void sendObjectWebhook(String callbackUrl, String topic, String key, Object object) {
        if (callbackUrl == null || callbackUrl.trim().isEmpty()) {
            logger.debug("No callback URL provided, skipping object webhook for topic: {}", topic);
            return;
        }

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("topic", topic);
            payload.put("key", key);
            payload.put("data", object);
            payload.put("timestamp", System.currentTimeMillis());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

            logger.debug("Sending object webhook to: {} for topic: {}", callbackUrl, topic);

            ResponseEntity<String> response = restTemplate.postForEntity(callbackUrl, request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                logger.debug("Object webhook sent successfully to: {} for topic: {}", callbackUrl, topic);
            } else {
                logger.warn("Object webhook failed with status: {} for URL: {} topic: {}", 
                           response.getStatusCode(), callbackUrl, topic);
            }

        } catch (ResourceAccessException e) {
            logger.error("Network error sending object webhook to: {} for topic: {}", callbackUrl, topic, e);
        } catch (Exception e) {
            logger.error("Error sending object webhook to: {} for topic: {}", callbackUrl, topic, e);
        }
    }
}