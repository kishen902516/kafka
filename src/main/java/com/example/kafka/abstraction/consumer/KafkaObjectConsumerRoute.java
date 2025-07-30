package com.example.kafka.abstraction.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaObjectConsumerRoute<T> extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaObjectConsumerRoute.class);

    private final String topic;
    private final String groupId;
    private final KafkaObjectHandler<T> objectHandler;
    private final Class<T> objectType;
    private final ObjectMapper objectMapper;
    private final String routeId;

    public KafkaObjectConsumerRoute(String topic, String groupId, KafkaObjectHandler<T> objectHandler, 
                                   Class<T> objectType, ObjectMapper objectMapper) {
        this.topic = topic;
        this.groupId = groupId;
        this.objectHandler = objectHandler;
        this.objectType = objectType;
        this.objectMapper = objectMapper;
        this.routeId = "kafka-object-consumer-" + topic + "-" + groupId;
    }

    @Override
    public void configure() throws Exception {
        from("kafka:" + topic + "?groupId=" + groupId)
            .routeId(routeId)
            .log("Received object message from topic: " + topic)
            .process(new ObjectProcessor())
            .onException(Exception.class)
                .handled(true)
                .log("Error processing object message from topic: " + topic + " - ${exception.message}")
                .to("log:error")
            .end();
    }

    private class ObjectProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            String key = exchange.getIn().getHeader("kafka.KEY", String.class);
            String messageBody = exchange.getIn().getBody(String.class);
            
            logger.debug("Processing object message from topic: {}, key: {}", topic, key);
            
            try {
                T object = objectMapper.readValue(messageBody, objectType);
                objectHandler.handle(topic, key, object, exchange);
                logger.debug("Successfully processed object message from topic: {}", topic);
            } catch (Exception e) {
                logger.error("Error processing object message from topic: {}", topic, e);
                throw new KafkaConsumerException("Failed to process object message from topic: " + topic, e);
            }
        }
    }
}