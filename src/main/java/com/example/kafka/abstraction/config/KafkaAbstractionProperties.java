package com.example.kafka.abstraction.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "kafka.abstraction")
@Validated
public class KafkaAbstractionProperties {

    @NotBlank
    private String bootstrapServers;

    @NotNull
    private Security security = new Security();

    @NotNull
    private Producer producer = new Producer();

    @NotNull
    private Consumer consumer = new Consumer();

    private Map<String, Object> additionalProperties = new HashMap<>();

    public static class Security {
        private String protocol = "SSL";
        private String keystoreLocation;
        private String keystorePassword;
        private String keystoreType = "JKS";
        private String truststoreLocation;
        private String truststorePassword;
        private String truststoreType = "JKS";
        private String keyPassword;
        private String sslEndpointIdentificationAlgorithm = "";
        private boolean enabled = false;

        public String getProtocol() {
            return protocol;
        }

        public void setProtocol(String protocol) {
            this.protocol = protocol;
        }

        public String getKeystoreLocation() {
            return keystoreLocation;
        }

        public void setKeystoreLocation(String keystoreLocation) {
            this.keystoreLocation = keystoreLocation;
        }

        public String getKeystorePassword() {
            return keystorePassword;
        }

        public void setKeystorePassword(String keystorePassword) {
            this.keystorePassword = keystorePassword;
        }

        public String getKeystoreType() {
            return keystoreType;
        }

        public void setKeystoreType(String keystoreType) {
            this.keystoreType = keystoreType;
        }

        public String getTruststoreLocation() {
            return truststoreLocation;
        }

        public void setTruststoreLocation(String truststoreLocation) {
            this.truststoreLocation = truststoreLocation;
        }

        public String getTruststorePassword() {
            return truststorePassword;
        }

        public void setTruststorePassword(String truststorePassword) {
            this.truststorePassword = truststorePassword;
        }

        public String getTruststoreType() {
            return truststoreType;
        }

        public void setTruststoreType(String truststoreType) {
            this.truststoreType = truststoreType;
        }

        public String getKeyPassword() {
            return keyPassword;
        }

        public void setKeyPassword(String keyPassword) {
            this.keyPassword = keyPassword;
        }

        public String getSslEndpointIdentificationAlgorithm() {
            return sslEndpointIdentificationAlgorithm;
        }

        public void setSslEndpointIdentificationAlgorithm(String sslEndpointIdentificationAlgorithm) {
            this.sslEndpointIdentificationAlgorithm = sslEndpointIdentificationAlgorithm;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Producer {
        private String keySerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String valueSerializer = "org.apache.kafka.common.serialization.StringSerializer";
        private String acks = "all";
        private int retries = 3;
        private int batchSize = 16384;
        private int lingerMs = 1;
        private long bufferMemory = 33554432L;
        private String compressionType = "snappy";
        private int maxInFlightRequestsPerConnection = 5;
        private boolean enableIdempotence = true;
        private int requestTimeoutMs = 30000;
        private int deliveryTimeoutMs = 120000;

        public String getKeySerializer() {
            return keySerializer;
        }

        public void setKeySerializer(String keySerializer) {
            this.keySerializer = keySerializer;
        }

        public String getValueSerializer() {
            return valueSerializer;
        }

        public void setValueSerializer(String valueSerializer) {
            this.valueSerializer = valueSerializer;
        }

        public String getAcks() {
            return acks;
        }

        public void setAcks(String acks) {
            this.acks = acks;
        }

        public int getRetries() {
            return retries;
        }

        public void setRetries(int retries) {
            this.retries = retries;
        }

        public int getBatchSize() {
            return batchSize;
        }

        public void setBatchSize(int batchSize) {
            this.batchSize = batchSize;
        }

        public int getLingerMs() {
            return lingerMs;
        }

        public void setLingerMs(int lingerMs) {
            this.lingerMs = lingerMs;
        }

        public long getBufferMemory() {
            return bufferMemory;
        }

        public void setBufferMemory(long bufferMemory) {
            this.bufferMemory = bufferMemory;
        }

        public String getCompressionType() {
            return compressionType;
        }

        public void setCompressionType(String compressionType) {
            this.compressionType = compressionType;
        }

        public int getMaxInFlightRequestsPerConnection() {
            return maxInFlightRequestsPerConnection;
        }

        public void setMaxInFlightRequestsPerConnection(int maxInFlightRequestsPerConnection) {
            this.maxInFlightRequestsPerConnection = maxInFlightRequestsPerConnection;
        }

        public boolean isEnableIdempotence() {
            return enableIdempotence;
        }

        public void setEnableIdempotence(boolean enableIdempotence) {
            this.enableIdempotence = enableIdempotence;
        }

        public int getRequestTimeoutMs() {
            return requestTimeoutMs;
        }

        public void setRequestTimeoutMs(int requestTimeoutMs) {
            this.requestTimeoutMs = requestTimeoutMs;
        }

        public int getDeliveryTimeoutMs() {
            return deliveryTimeoutMs;
        }

        public void setDeliveryTimeoutMs(int deliveryTimeoutMs) {
            this.deliveryTimeoutMs = deliveryTimeoutMs;
        }
    }

    public static class Consumer {
        private String keyDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String valueDeserializer = "org.apache.kafka.common.serialization.StringDeserializer";
        private String groupId = "default-group";
        private String autoOffsetReset = "earliest";
        private boolean enableAutoCommit = false;
        private int autoCommitIntervalMs = 5000;
        private int sessionTimeoutMs = 30000;
        private int heartbeatIntervalMs = 3000;
        private int maxPollRecords = 500;
        private int maxPollIntervalMs = 300000;
        private int fetchMinBytes = 1;
        private int fetchMaxWaitMs = 500;

        public String getKeyDeserializer() {
            return keyDeserializer;
        }

        public void setKeyDeserializer(String keyDeserializer) {
            this.keyDeserializer = keyDeserializer;
        }

        public String getValueDeserializer() {
            return valueDeserializer;
        }

        public void setValueDeserializer(String valueDeserializer) {
            this.valueDeserializer = valueDeserializer;
        }

        public String getGroupId() {
            return groupId;
        }

        public void setGroupId(String groupId) {
            this.groupId = groupId;
        }

        public String getAutoOffsetReset() {
            return autoOffsetReset;
        }

        public void setAutoOffsetReset(String autoOffsetReset) {
            this.autoOffsetReset = autoOffsetReset;
        }

        public boolean isEnableAutoCommit() {
            return enableAutoCommit;
        }

        public void setEnableAutoCommit(boolean enableAutoCommit) {
            this.enableAutoCommit = enableAutoCommit;
        }

        public int getAutoCommitIntervalMs() {
            return autoCommitIntervalMs;
        }

        public void setAutoCommitIntervalMs(int autoCommitIntervalMs) {
            this.autoCommitIntervalMs = autoCommitIntervalMs;
        }

        public int getSessionTimeoutMs() {
            return sessionTimeoutMs;
        }

        public void setSessionTimeoutMs(int sessionTimeoutMs) {
            this.sessionTimeoutMs = sessionTimeoutMs;
        }

        public int getHeartbeatIntervalMs() {
            return heartbeatIntervalMs;
        }

        public void setHeartbeatIntervalMs(int heartbeatIntervalMs) {
            this.heartbeatIntervalMs = heartbeatIntervalMs;
        }

        public int getMaxPollRecords() {
            return maxPollRecords;
        }

        public void setMaxPollRecords(int maxPollRecords) {
            this.maxPollRecords = maxPollRecords;
        }

        public int getMaxPollIntervalMs() {
            return maxPollIntervalMs;
        }

        public void setMaxPollIntervalMs(int maxPollIntervalMs) {
            this.maxPollIntervalMs = maxPollIntervalMs;
        }

        public int getFetchMinBytes() {
            return fetchMinBytes;
        }

        public void setFetchMinBytes(int fetchMinBytes) {
            this.fetchMinBytes = fetchMinBytes;
        }

        public int getFetchMaxWaitMs() {
            return fetchMaxWaitMs;
        }

        public void setFetchMaxWaitMs(int fetchMaxWaitMs) {
            this.fetchMaxWaitMs = fetchMaxWaitMs;
        }
    }

    public String getBootstrapServers() {
        return bootstrapServers;
    }

    public void setBootstrapServers(String bootstrapServers) {
        this.bootstrapServers = bootstrapServers;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public Producer getProducer() {
        return producer;
    }

    public void setProducer(Producer producer) {
        this.producer = producer;
    }

    public Consumer getConsumer() {
        return consumer;
    }

    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }
}