package com.example.kafka.abstraction.consumer;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KafkaConsumerRoute extends RouteBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerRoute.class);

    private final String topic;
    private final String groupId;
    private final KafkaMessageHandler messageHandler;
    private final String routeId;

    public KafkaConsumerRoute(String topic, String groupId, KafkaMessageHandler messageHandler) {
        this.topic = topic;
        this.groupId = groupId;
        this.messageHandler = messageHandler;
        this.routeId = "kafka-consumer-" + topic + "-" + groupId;
    }

    @Override
    public void configure() throws Exception {
        from("kafka:" + topic + "?groupId=" + groupId)
            .routeId(routeId)
            .log("Received message from topic: " + topic)
            .process(new MessageProcessor())
            .onException(Exception.class)
                .handled(true)
                .log("Error processing message from topic: " + topic + " - ${exception.message}")
                .to("log:error")
            .end();
    }

    private class MessageProcessor implements Processor {
        @Override
        public void process(Exchange exchange) throws Exception {
            String key = exchange.getIn().getHeader("kafka.KEY", String.class);
            String message = exchange.getIn().getBody(String.class);
            
            logger.debug("Processing message from topic: {}, key: {}", topic, key);
            
            try {
                messageHandler.handle(topic, key, message, exchange);
                logger.debug("Successfully processed message from topic: {}", topic);
            } catch (Exception e) {
                logger.error("Error processing message from topic: {}", topic, e);
                throw new KafkaConsumerException("Failed to process message from topic: " + topic, e);
            }
        }
    }
}