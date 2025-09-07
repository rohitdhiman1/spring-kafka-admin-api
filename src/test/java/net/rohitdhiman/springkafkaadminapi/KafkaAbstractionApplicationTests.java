package net.rohitdhiman.springkafkaadminapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

// @SpringBootTest: This annotation tells Spring Boot to look for a main configuration class
// (one with @SpringBootApplication, like KafkaAbstractionApplication.java) and create an
// application context for the test.
@SpringBootTest
class KafkaAbstractionApplicationTests {

    // This is a simple smoke test to ensure the application context loads successfully.
    // If it throws an exception, the test will fail, indicating a problem with the
    // application's configuration or a missing bean.
    @Test
    void contextLoads() {
    }

}
