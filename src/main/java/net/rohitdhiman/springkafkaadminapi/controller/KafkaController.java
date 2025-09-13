package net.rohitdhiman.springkafkaadminapi.controller;

import net.rohitdhiman.springkafkaadminapi.service.KafkaService;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

// @RestController combines @Controller and @ResponseBody, meaning it's a controller
// where every method returns a domain object instead of a view.
@RestController
@RequestMapping("/api")
public class KafkaController {

    private final KafkaService kafkaService;

    // Constructor injection is the recommended way to inject dependencies in Spring.
    // The KafkaService instance is automatically provided by Spring's IoC container.
    public KafkaController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping("/cluster")
    public ResponseEntity<Collection<Node>> describeCluster() throws ExecutionException, InterruptedException {
        Collection<Node> nodes = kafkaService.describeCluster();
        return ResponseEntity.ok(nodes);
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<Collection<ConsumerGroupListing>> listConsumerGroups() throws ExecutionException, InterruptedException {
        Collection<ConsumerGroupListing> consumerGroups = kafkaService.listConsumerGroups();
        return ResponseEntity.ok(consumerGroups);
    }

    @GetMapping("/consumer-groups/{groupId}")
    public ResponseEntity<ConsumerGroupDescription> describeConsumerGroup(@PathVariable String groupId) throws ExecutionException, InterruptedException {
        Map<String, ConsumerGroupDescription> description = kafkaService.describeConsumerGroups(List.of(groupId));
        ConsumerGroupDescription consumerGroupDescription = description.get(groupId);

        if (consumerGroupDescription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(consumerGroupDescription);
    }

    // A record for the topic creation request. Records are a concise way to create
    // immutable data classes in Java.
    public record CreateTopicRequest(String topicName) {}

    public record MessageResponse(String message) {}

    // Wrapper methods for HATEOAS link generation (no checked exceptions)
    public ResponseEntity<TopicDescription> describeTopicNoException(String topicName) {
        try {
            return describeTopic(topicName);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    public ResponseEntity<MessageResponse> deleteTopicNoException(String topicName) {
        try {
            return deleteTopic(topicName);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET /api/topics
    // Endpoint to list all topics in the Kafka cluster.
    @GetMapping("/topics")
    public ResponseEntity<CollectionModel<EntityModel<Map<String, Object>>>> listTopics() throws ExecutionException, InterruptedException {
        List<String> topics = kafkaService.listTopics().names().get()
                .stream()
                .collect(Collectors.toList());

        List<EntityModel<Map<String, Object>>> topicResources = topics.stream().map(topicName -> {
            Map<String, Object> topicInfo = Map.of(
                "name", topicName,
                "status", "available",
                "message", "Kafka topic resource"
            );
            EntityModel<Map<String, Object>> resource = EntityModel.of(topicInfo);
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(KafkaController.class).describeTopicNoException(topicName)).withSelfRel());
            resource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(KafkaController.class).deleteTopicNoException(topicName)).withRel("delete"));
            return resource;
        }).collect(Collectors.toList());

        CollectionModel<EntityModel<Map<String, Object>>> collectionModel = CollectionModel.of(topicResources);
        collectionModel.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(KafkaController.class).listTopics()).withSelfRel());
        return ResponseEntity.ok(collectionModel);
    }

    // POST /api/topics
    // Endpoint to create a new topic. The topic name is provided in the request body.
    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageResponse> createTopic(@RequestBody CreateTopicRequest request) throws ExecutionException, InterruptedException {
        kafkaService.createTopic(request.topicName());
        var response = new MessageResponse("Topic '" + request.topicName() + "' created successfully.");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET /api/topics/{topicName}
    // Endpoint to describe a specific topic, providing details like partitions and replication factor.
    @GetMapping("/topics/{topicName}")
    public ResponseEntity<TopicDescription> describeTopic(@PathVariable String topicName) throws ExecutionException, InterruptedException {
        // We'll call a new method in the service to get the topic's description.
        // This method will need to be added to the KafkaService class.
        Map<String, TopicDescription> description = kafkaService.describeTopics(List.of(topicName));
        TopicDescription topicDescription = description.get(topicName);

        if (topicDescription == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(topicDescription);
    }

    // DELETE /api/topics/{topicName}
    // Endpoint to delete a specific topic.
    @DeleteMapping("/topics/{topicName}")
    public ResponseEntity<MessageResponse> deleteTopic(@PathVariable String topicName) throws ExecutionException, InterruptedException {
        // We'll call another new method in the service to handle the topic deletion.
        // This method will also need to be added to the KafkaService class.
        kafkaService.deleteTopic(topicName);
        var response = new MessageResponse("Topic '" + topicName + "' deleted successfully.");
        return ResponseEntity.ok(response);
    }
}
