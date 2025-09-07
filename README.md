# Spring Kafka Admin API

This project provides a RESTful API to manage Apache Kafka topics. It's a Spring Boot application that uses the Kafka AdminClient to interact with a Kafka cluster. The API allows you to perform basic administrative tasks on Kafka topics, such as creating, listing, describing, and deleting them.

This provides a simple and convenient way to manage your Kafka topics programmatically without having to use the command-line tools that come with Kafka.

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

To stop all running containers and remove the network created by Docker Compose, run the following command:

```bash
docker-compose down
```

This command is the standard way to tear down a Docker Compose environment. It stops and removes the containers, which is exactly what you need to do to clean up your local Docker environment after you're done.

## API Usage

Here are some examples of how to use the API with `curl`.

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
