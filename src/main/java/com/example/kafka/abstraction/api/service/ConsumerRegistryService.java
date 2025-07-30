package com.example.kafka.abstraction.api.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ConsumerRegistryService {

    private final ConcurrentMap<String, String> consumerCallbacks = new ConcurrentHashMap<>();

    public void registerConsumer(String topic, String groupId, String callbackUrl) {
        String key = createKey(topic, groupId);
        consumerCallbacks.put(key, callbackUrl);
    }

    public void unregisterConsumer(String topic, String groupId) {
        String key = createKey(topic, groupId);
        consumerCallbacks.remove(key);
    }

    public String getCallbackUrl(String topic, String groupId) {
        String key = createKey(topic, groupId);
        return consumerCallbacks.get(key);
    }

    public boolean hasConsumer(String topic, String groupId) {
        String key = createKey(topic, groupId);
        return consumerCallbacks.containsKey(key);
    }

    private String createKey(String topic, String groupId) {
        return topic + "-" + groupId;
    }

    public ConcurrentMap<String, String> getAllConsumers() {
        return new ConcurrentHashMap<>(consumerCallbacks);
    }
}