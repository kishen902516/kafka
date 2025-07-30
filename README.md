# Kafka Abstraction Library

A Spring Boot and Apache Camel based Kafka abstraction library that provides simplified producer and consumer APIs with SSL/TLS certificate support for Confluent Kafka clusters.

## Features

- **Easy Integration**: Auto-configuration for Spring Boot applications
- **SSL/TLS Support**: Full support for certificate-based authentication and authorization (ACLs)
- **Producer Abstraction**: Simple API for sending messages and objects
- **Consumer Abstraction**: Easy subscription management with message handlers
- **Camel Integration**: Built on Apache Camel for robust message routing
- **Async Support**: Asynchronous message sending capabilities
- **Type Safety**: Generic support for object serialization/deserialization
- **Configurable**: Extensive configuration options for both producer and consumer

## Dependencies

- Spring Boot 3.2.0+
- Apache Camel 4.2.0+
- Apache Kafka 3.6.0+
- Java 17+

## Quick Start

### 1. Add Dependency

Add the library to your Spring Boot project's `pom.xml`:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>kafka-abstraction</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configuration

Configure your Kafka connection in `application.yml`:

```yaml
kafka:
  abstraction:
    bootstrap-servers: your-kafka-cluster:9092
    
    security:
      enabled: true
      keystore-location: /path/to/client.keystore.jks
      keystore-password: your-keystore-password
      truststore-location: /path/to/client.truststore.jks
      truststore-password: your-truststore-password
      
    consumer:
      group-id: your-consumer-group
```

### 3. Usage

#### Producer Usage

```java
@Service
public class MessageService {
    
    private final KafkaProducerService producerService;
    
    public MessageService(KafkaProducerService producerService) {
        this.producerService = producerService;
    }
    
    public void sendMessage() {
        // Send simple string message
        producerService.send("my-topic", "Hello Kafka!");
        
        // Send message with key
        producerService.send("my-topic", "user-123", "User data");
        
        // Send object
        User user = new User("123", "John Doe");
        producerService.sendObject("user-topic", user);
        
        // Send async
        producerService.sendAsync("my-topic", "Async message")
            .thenRun(() -> System.out.println("Message sent successfully"));
    }
}
```

#### Consumer Usage

```java
@Service
public class MessageConsumer {
    
    private final KafkaConsumerService consumerService;
    
    public MessageConsumer(KafkaConsumerService consumerService) {
        this.consumerService = consumerService;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void setupConsumers() {
        // Subscribe to string messages
        consumerService.subscribe("my-topic", "my-group", 
            (topic, key, message, exchange) -> {
                System.out.println("Received: " + message);
            });
        
        // Subscribe to object messages
        consumerService.subscribeForObject("user-topic", "user-group", User.class,
            (topic, key, user, exchange) -> {
                System.out.println("Received user: " + user.getName());
            });
    }
}
```

## SSL/TLS Configuration

For Confluent Kafka clusters with certificate-based authentication:

```yaml
kafka:
  abstraction:
    bootstrap-servers: pkc-xxxxx.region.provider.confluent.cloud:9092
    
    security:
      enabled: true
      protocol: SSL
      keystore-location: /path/to/client.keystore.jks
      keystore-password: your-keystore-password
      keystore-type: JKS
      truststore-location: /path/to/client.truststore.jks
      truststore-password: your-truststore-password
      truststore-type: JKS
      key-password: your-key-password  # Optional, if different from keystore password
      ssl-endpoint-identification-algorithm: ""  # Empty for self-signed certificates
```

### Creating Keystores and Truststores

For Confluent Cloud or self-managed Kafka with SSL:

1. **Client Certificate** (for authentication):
   ```bash
   # Convert client certificate to JKS keystore
   keytool -importkeystore -srckeystore client.p12 -srcstoretype PKCS12 \
           -destkeystore client.keystore.jks -deststoretype JKS
   ```

2. **CA Certificate** (for server verification):
   ```bash
   # Import CA certificate to truststore
   keytool -import -file ca-cert.pem -alias ca-cert \
           -keystore client.truststore.jks -storepass changeit
   ```

## Advanced Configuration

### Producer Settings

```yaml
kafka:
  abstraction:
    producer:
      acks: all                    # Wait for all replicas
      retries: 3                   # Number of retries
      batch-size: 16384           # Batch size in bytes
      linger-ms: 1                # Time to wait for batching
      compression-type: snappy     # Compression algorithm
      enable-idempotence: true     # Exactly-once semantics
```

### Consumer Settings

```yaml
kafka:
  abstraction:
    consumer:
      auto-offset-reset: earliest  # Start from beginning
      enable-auto-commit: false    # Manual commit control
      max-poll-records: 500        # Max records per poll
      session-timeout-ms: 30000    # Session timeout
```

## Error Handling

The library provides custom exceptions for different scenarios:

- `KafkaProducerException`: Thrown when message sending fails
- `KafkaConsumerException`: Thrown when message consumption fails
- `CertificateException`: Thrown when SSL certificate configuration is invalid

```java
try {
    producerService.send("topic", "message");
} catch (KafkaProducerException e) {
    log.error("Failed to send message", e);
    // Handle error appropriately
}
```

## Testing

Include test dependencies and use embedded Kafka for testing:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka-test</artifactId>
    <scope>test</scope>
</dependency>
```

## Best Practices

1. **Security**: Always use SSL/TLS in production environments
2. **Consumer Groups**: Use meaningful consumer group IDs for your teams
3. **Error Handling**: Implement proper error handling and retry logic
4. **Monitoring**: Monitor consumer lag and producer metrics
5. **Schema Evolution**: Consider using Avro or Schema Registry for object serialization
6. **Resource Management**: Properly manage consumer subscriptions and unsubscribe when needed

## Team Usage

Different teams can use this abstraction by:

1. **Configuration**: Each team maintains their own `application.yml` with their specific:
   - Consumer group IDs
   - Topic names
   - Certificate paths
   
2. **Service Implementation**: Teams implement their own message handlers:
   ```java
   @Service
   public class TeamAMessageService {
       // Team A specific logic
   }
   ```

3. **Certificate Management**: Teams manage their own certificates for ACL-based authorization

## Example Application

See `ExampleKafkaUsage.java` for a complete working example. Enable it by setting:

```yaml
kafka:
  abstraction:
    example:
      enabled: true
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Submit a pull request

## License

This project is licensed under the MIT License.