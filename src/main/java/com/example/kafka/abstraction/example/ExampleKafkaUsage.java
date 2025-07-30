package com.example.kafka.abstraction.example;

import com.example.kafka.abstraction.consumer.KafkaConsumerService;
import com.example.kafka.abstraction.producer.KafkaProducerService;
import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "kafka.abstraction.example", name = "enabled", havingValue = "true")
public class ExampleKafkaUsage implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ExampleKafkaUsage.class);

    private final KafkaProducerService producerService;
    private final KafkaConsumerService consumerService;

    public ExampleKafkaUsage(KafkaProducerService producerService, KafkaConsumerService consumerService) {
        this.producerService = producerService;
        this.consumerService = consumerService;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting Kafka abstraction example");

        String topic = "example-topic";
        String groupId = "example-group";

        subscribeToStringMessages(topic, groupId);
        subscribeToObjectMessages(topic, groupId);

        Thread.sleep(2000);

        sendStringMessages(topic);
        sendObjectMessages(topic);

        logger.info("Kafka abstraction example completed");
    }

    private void subscribeToStringMessages(String topic, String groupId) {
        consumerService.subscribe(topic, groupId, (topicName, key, message, exchange) -> {
            logger.info("Received string message from topic: {}, key: {}, message: {}", topicName, key, message);
            
        });
        logger.info("Subscribed to string messages on topic: {}", topic);
    }

    private void subscribeToObjectMessages(String topic, String groupId) {
        consumerService.subscribeForObject(topic, groupId + "-object", ExampleMessage.class, 
            (topicName, key, message, exchange) -> {
                logger.info("Received object message from topic: {}, key: {}, message: {}", 
                           topicName, key, message);
            });
        logger.info("Subscribed to object messages on topic: {}", topic);
    }

    private void sendStringMessages(String topic) {
        producerService.send(topic, "Hello Kafka!");
        producerService.send(topic, "key1", "Hello with key!");
        logger.info("Sent string messages to topic: {}", topic);
    }

    private void sendObjectMessages(String topic) {
        ExampleMessage message1 = new ExampleMessage("123", "John Doe", "john@example.com");
        ExampleMessage message2 = new ExampleMessage("456", "Jane Smith", "jane@example.com");

        producerService.sendObject(topic, message1);
        producerService.sendObject(topic, "user-456", message2);
        logger.info("Sent object messages to topic: {}", topic);
    }

    public static class ExampleMessage {
        private String id;
        private String name;
        private String email;

        public ExampleMessage() {
        }

        public ExampleMessage(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        @Override
        public String toString() {
            return "ExampleMessage{" +
                   "id='" + id + '\'' +
                   ", name='" + name + '\'' +
                   ", email='" + email + '\'' +
                   '}';
        }
    }
}