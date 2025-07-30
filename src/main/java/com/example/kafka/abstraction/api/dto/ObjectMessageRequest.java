package com.example.kafka.abstraction.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ObjectMessageRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    private String key;

    @NotNull(message = "Object data is required")
    private Object data;

    public ObjectMessageRequest() {
    }

    public ObjectMessageRequest(String topic, String key, Object data) {
        this.topic = topic;
        this.key = key;
        this.data = data;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ObjectMessageRequest{" +
               "topic='" + topic + '\'' +
               ", key='" + key + '\'' +
               ", data=" + data +
               '}';
    }
}