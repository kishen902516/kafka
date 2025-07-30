package com.example.kafka.abstraction.config;

import com.example.kafka.abstraction.consumer.KafkaConsumerService;
import com.example.kafka.abstraction.producer.KafkaProducerService;
import com.example.kafka.abstraction.security.CertificateManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import java.util.Properties;

@AutoConfiguration
@ConditionalOnClass({CamelContext.class, KafkaComponent.class})
@EnableConfigurationProperties(KafkaAbstractionProperties.class)
@ComponentScan(basePackages = "com.example.kafka.abstraction")
@ConditionalOnProperty(prefix = "kafka.abstraction", name = "bootstrap-servers")
public class KafkaAbstractionAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(KafkaAbstractionAutoConfiguration.class);

    private final KafkaAbstractionProperties properties;

    public KafkaAbstractionAutoConfiguration(KafkaAbstractionProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    @ConditionalOnMissingBean
    public CertificateManager certificateManager() {
        return new CertificateManager();
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaProducerService kafkaProducerService(ProducerTemplate producerTemplate) {
        return new KafkaProducerService(producerTemplate);
    }

    @Bean
    @ConditionalOnMissingBean
    public KafkaConsumerService kafkaConsumerService(CamelContext camelContext, ObjectMapper objectMapper) {
        return new KafkaConsumerService(camelContext, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean
    @Autowired
    public KafkaComponent kafkaComponent(CertificateManager certificateManager) {
        logger.info("Configuring Kafka component with bootstrap servers: {}", properties.getBootstrapServers());
        
        KafkaComponent kafkaComponent = new KafkaComponent();
        
        KafkaConfiguration kafkaConfiguration = new KafkaConfiguration();
        kafkaConfiguration.setBrokers(properties.getBootstrapServers());
        
        configureProducerSettings(kafkaConfiguration);
        configureConsumerSettings(kafkaConfiguration);
        
        if (properties.getSecurity().isEnabled()) {
            configureSslSettings(kafkaConfiguration, certificateManager);
        }
        
        kafkaComponent.setConfiguration(kafkaConfiguration);
        
        logger.info("Kafka component configured successfully");
        return kafkaComponent;
    }

    private void configureProducerSettings(KafkaConfiguration kafkaConfiguration) {
        KafkaAbstractionProperties.Producer producer = properties.getProducer();
        
        kafkaConfiguration.setKeySerializerClass(producer.getKeySerializer());
        kafkaConfiguration.setValueSerializerClass(producer.getValueSerializer());
        kafkaConfiguration.setRequestRequiredAcks(producer.getAcks());
        kafkaConfiguration.setRetryBackoffMs(producer.getRetries());
        kafkaConfiguration.setProducerBatchSize(producer.getBatchSize());
        kafkaConfiguration.setLingerMs(producer.getLingerMs());
        kafkaConfiguration.setBufferMemorySize(producer.getBufferMemory());
        kafkaConfiguration.setCompressionCodec(producer.getCompressionType());
        kafkaConfiguration.setMaxInFlightRequest(producer.getMaxInFlightRequestsPerConnection());
        kafkaConfiguration.setEnableIdempotence(producer.isEnableIdempotence());
        kafkaConfiguration.setRequestTimeoutMs(producer.getRequestTimeoutMs());
        kafkaConfiguration.setDeliveryTimeoutMs(producer.getDeliveryTimeoutMs());
        
        logger.debug("Producer settings configured");
    }

    private void configureConsumerSettings(KafkaConfiguration kafkaConfiguration) {
        KafkaAbstractionProperties.Consumer consumer = properties.getConsumer();
        
        kafkaConfiguration.setKeyDeserializer(consumer.getKeyDeserializer());
        kafkaConfiguration.setValueDeserializer(consumer.getValueDeserializer());
        kafkaConfiguration.setGroupId(consumer.getGroupId());
        kafkaConfiguration.setAutoOffsetReset(consumer.getAutoOffsetReset());
        kafkaConfiguration.setAutoCommitEnable(consumer.isEnableAutoCommit());
        kafkaConfiguration.setAutoCommitIntervalMs(consumer.getAutoCommitIntervalMs());
        kafkaConfiguration.setSessionTimeoutMs(consumer.getSessionTimeoutMs());
        kafkaConfiguration.setHeartbeatIntervalMs(consumer.getHeartbeatIntervalMs());
        kafkaConfiguration.setMaxPollRecords(consumer.getMaxPollRecords());
        kafkaConfiguration.setMaxPollIntervalMs(consumer.getMaxPollIntervalMs());
        kafkaConfiguration.setFetchMinBytes(consumer.getFetchMinBytes());
        kafkaConfiguration.setFetchWaitMaxMs(consumer.getFetchMaxWaitMs());
        
        logger.debug("Consumer settings configured");
    }

    private void configureSslSettings(KafkaConfiguration kafkaConfiguration, CertificateManager certificateManager) {
        KafkaAbstractionProperties.Security security = properties.getSecurity();
        
        try {
            Properties sslProperties = certificateManager.createSslProperties(
                security.getKeystoreLocation(),
                security.getKeystorePassword(),
                security.getKeystoreType(),
                security.getTruststoreLocation(),
                security.getTruststorePassword(),
                security.getTruststoreType(),
                security.getKeyPassword(),
                security.getSslEndpointIdentificationAlgorithm()
            );
            
            kafkaConfiguration.setSecurityProtocol(security.getProtocol());
            kafkaConfiguration.setSslKeystoreLocation(security.getKeystoreLocation());
            kafkaConfiguration.setSslKeystorePassword(security.getKeystorePassword());
            kafkaConfiguration.setSslKeystoreType(security.getKeystoreType());
            kafkaConfiguration.setSslTruststoreLocation(security.getTruststoreLocation());
            kafkaConfiguration.setSslTruststorePassword(security.getTruststorePassword());
            kafkaConfiguration.setSslTruststoreType(security.getTruststoreType());
            kafkaConfiguration.setSslKeyPassword(security.getKeyPassword());
            kafkaConfiguration.setSslEndpointAlgorithm(security.getSslEndpointIdentificationAlgorithm());
            
            logger.info("SSL/TLS configuration applied successfully");
            
        } catch (Exception e) {
            logger.error("Error configuring SSL settings", e);
            throw new RuntimeException("Failed to configure SSL settings", e);
        }
    }
}