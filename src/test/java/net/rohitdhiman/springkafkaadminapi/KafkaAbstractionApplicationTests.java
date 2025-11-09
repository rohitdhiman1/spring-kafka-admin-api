package net.rohitdhiman.springkafkaadminapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.kafka.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

// @SpringBootTest: This annotation tells Spring Boot to look for a main configuration class
// (one with @SpringBootApplication, like KafkaAbstractionApplication.java) and create an
// application context for the test.
// @Testcontainers: This annotation enables Testcontainers support for JUnit 5.
// Testcontainers will automatically start and stop Docker containers for testing.
@SpringBootTest
@Testcontainers
class KafkaAbstractionApplicationTests {

    // @Container: This annotation tells Testcontainers to manage this container's lifecycle.
    // The container will be started before tests and stopped after tests.
    // Using Apache Kafka 3.8.1 (latest stable) with the new KafkaContainer from org.testcontainers.kafka
    @Container
    static KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("apache/kafka:3.8.1"));

    // This method dynamically sets the spring.kafka.bootstrap-servers property
    // to point to the Testcontainers-managed Kafka instance.
    @DynamicPropertySource
    static void setKafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
    }

    // This is a simple smoke test to ensure the application context loads successfully
    // with a real Kafka instance running in a Testcontainer.
    // If it throws an exception, the test will fail, indicating a problem with the
    // application's configuration or a missing bean.
    @Test
    void contextLoads() {
    }

}
