# Spring Kafka Admin API

This project provides a RESTful API to manage Apache Kafka topics, consumer groups, and view cluster information. It's a Spring Boot application that uses the Kafka AdminClient to interact with a Kafka cluster. The API allows you to perform basic administrative tasks on Kafka resources, such as creating, listing, describing, and deleting them, as well as viewing information about the cluster nodes.

This provides a simple and convenient way to manage your Kafka resources programmatically without having to use the command-line tools that come with Kafka.

## Prerequisites

- Java 17 or later
- Maven
- Docker
- Docker Compose

## How to run locally

This project is configured to run entirely within Docker containers, which simplifies setup and ensures a consistent environment.

1.  **Package the Spring Boot Application:**

    First, build the application's JAR file using Maven. This command cleans the project and packages it into a `.jar` file in the `target/` directory.

    ```bash
    mvn clean package
    ```

2.  **Build and Run All Services:**

    Use Docker Compose to build the Spring Boot application image and start all the services (Spring app, Kafka, Zookeeper, and Kafdrop).

    ```bash
    docker-compose up --build
    ```

    -   The `--build` flag tells Docker Compose to rebuild the `spring-app` image to include your latest code changes.
    -   Your application API will be available at `http://localhost:8080`.
    -   You can view the Kafka cluster UI (Kafdrop) at `http://localhost:9000`.

## How to Stop and Tear Down the Environment

To cleanly stop all running containers, remove them, and tear down the network created by Docker Compose, run the following command:

```bash
docker-compose down
```

This command is the standard and safest way to tear down a Docker Compose environment. It performs the following actions:

-   **Stops** all the containers defined in your `docker-compose.yml` file.
-   **Removes** the stopped containers.
-   **Removes** the networks that were created for the services.

### Removing Volumes (Optional)

By default, `docker-compose down` does not remove the volumes created by your services (like the Kafka data volume). This is a safety feature to prevent data loss. If you want to remove the volumes as well for a complete cleanup, use the `-v` flag:

```bash
docker-compose down -v
```

This is useful when you want to start from a completely fresh state.

## API Documentation

This project uses `springdoc-openapi` to automatically generate interactive API documentation. Once the application is running, you can access the documentation in your browser:

-   **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
-   **OpenAPI Spec**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

The Swagger UI allows you to explore and test all the API endpoints directly from your browser.

## API Usage


## Health Check

This project exposes a health check endpoint using Spring Boot Actuator. You can use this endpoint to verify that the application is running and healthy.

### Health Check Endpoint

Returns the health status of the application and its dependencies (such as Kafka).

```bash
curl -X GET http://localhost:8080/actuator/health
```

**Sample Response:**

```json
{
    "status": "UP",
    "components": {
        "kafka": {
            "status": "UP",
            "details": {
                "clusterId": "...",
                "brokers": [ ... ]
            }
        },
        ...
    }
}
```

You can also view detailed health information in your browser at [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health).

---

Here are some examples of how to use the API with `curl`.

### Describe the Cluster

Provides information about the brokers in the Kafka cluster.

```bash
curl -X GET http://localhost:8080/api/cluster
```

### List all topics

```bash
curl -X GET http://localhost:8080/api/topics
```

### Create a new topic

```bash
curl -X POST http://localhost:8080/api/topics \
-H "Content-Type: application/json" \
-d '{"topicName": "my-new-topic"}'
```

### Describe a topic

```bash
curl -X GET http://localhost:8080/api/topics/my-new-topic
```

### Delete a topic

```bash
curl -X DELETE http://localhost:8080/api/topics/my-new-topic
```

### List all consumer groups

```bash
curl -X GET http://localhost:8080/api/consumer-groups
```

### Describe a consumer group

```bash
curl -X GET http://localhost:8080/api/consumer-groups/my-consumer-group
```
