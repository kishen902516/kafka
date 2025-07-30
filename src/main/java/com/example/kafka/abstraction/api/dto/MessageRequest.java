package com.example.kafka.abstraction.api.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    private String key;

    @NotBlank(message = "Message is required")
    private String message;

    public MessageRequest() {
    }

    public MessageRequest(String topic, String key, String message) {
        this.topic = topic;
        this.key = key;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
               "topic='" + topic + '\'' +
               ", key='" + key + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}