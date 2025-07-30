package com.example.kafka.abstraction.api.dto;

import jakarta.validation.constraints.NotBlank;

public class ConsumerSubscriptionRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    @NotBlank(message = "Group ID is required")
    private String groupId;

    private String callbackUrl;

    public ConsumerSubscriptionRequest() {
    }

    public ConsumerSubscriptionRequest(String topic, String groupId, String callbackUrl) {
        this.topic = topic;
        this.groupId = groupId;
        this.callbackUrl = callbackUrl;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String toString() {
        return "ConsumerSubscriptionRequest{" +
               "topic='" + topic + '\'' +
               ", groupId='" + groupId + '\'' +
               ", callbackUrl='" + callbackUrl + '\'' +
               '}';
    }
}