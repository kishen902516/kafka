package com.example.kafka.abstraction.api.dto;

public class ConsumerStatusResponse {

    private String topic;
    private String groupId;
    private boolean subscribed;
    private String status;

    public ConsumerStatusResponse() {
    }

    public ConsumerStatusResponse(String topic, String groupId, boolean subscribed, String status) {
        this.topic = topic;
        this.groupId = groupId;
        this.subscribed = subscribed;
        this.status = status;
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

    public boolean isSubscribed() {
        return subscribed;
    }

    public void setSubscribed(boolean subscribed) {
        this.subscribed = subscribed;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ConsumerStatusResponse{" +
               "topic='" + topic + '\'' +
               ", groupId='" + groupId + '\'' +
               ", subscribed=" + subscribed +
               ", status='" + status + '\'' +
               '}';
    }
}