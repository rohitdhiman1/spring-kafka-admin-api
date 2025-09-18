package net.rohitdhiman.springkafkaadminapi.controller;

import net.rohitdhiman.springkafkaadminapi.service.KafkaService;
import org.apache.kafka.clients.admin.ConsumerGroupDescription;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.common.Node;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api")
public class KafkaController {

    private final KafkaService kafkaService;

    public KafkaController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @GetMapping("/cluster")
    public ResponseEntity<Collection<Node>> describeCluster() {
        try {
            Collection<Node> nodes = kafkaService.describeCluster();
            return ResponseEntity.ok(nodes);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/consumer-groups")
    public ResponseEntity<Collection<ConsumerGroupListing>> listConsumerGroups() {
        try {
            Collection<ConsumerGroupListing> consumerGroups = kafkaService.listConsumerGroups();
            return ResponseEntity.ok(consumerGroups);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/consumer-groups/{groupId}")
    public ResponseEntity<ConsumerGroupDescription> describeConsumerGroup(@PathVariable String groupId) {
        try {
            Map<String, ConsumerGroupDescription> description = kafkaService.describeConsumerGroups(List.of(groupId));
            ConsumerGroupDescription consumerGroupDescription = description.get(groupId);

            if (consumerGroupDescription == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(consumerGroupDescription);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/topics/under-replicated")
    public ResponseEntity<List<String>> getUnderReplicatedTopics() {
        try {
            List<String> underReplicatedTopics = kafkaService.findUnderReplicatedTopics();
            return ResponseEntity.ok(underReplicatedTopics);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public record CreateTopicRequest(String topicName) {}

    public record MessageResponse(String message) {}

    @GetMapping("/topics")
    public ResponseEntity<CollectionModel<EntityModel<Map<String, Object>>>> listTopics() {
        try {
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
                
                // HATEOAS links
                resource.add(linkTo(methodOn(KafkaController.class).describeTopic(topicName)).withSelfRel());
                resource.add(linkTo(methodOn(KafkaController.class).deleteTopic(topicName)).withRel("delete"));
                
                return resource;
            }).collect(Collectors.toList());

            CollectionModel<EntityModel<Map<String, Object>>> collectionModel = CollectionModel.of(topicResources);
            collectionModel.add(linkTo(methodOn(KafkaController.class).listTopics()).withSelfRel());
            return ResponseEntity.ok(collectionModel);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/topics")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<MessageResponse> createTopic(@RequestBody CreateTopicRequest request) {
        try {
            kafkaService.createTopic(request.topicName());
            var response = new MessageResponse("Topic '" + request.topicName() + "' created successfully.");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (net.rohitdhiman.springkafkaadminapi.exception.TopicAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MessageResponse("Topic '" + request.topicName() + "' already exists."));
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error creating topic: " + e.getMessage()));
        }
    }

    @GetMapping("/topics/{topicName}")
    public ResponseEntity<?> describeTopic(@PathVariable String topicName) {
        try {
            Map<String, TopicDescription> description = kafkaService.describeTopics(List.of(topicName));
            TopicDescription topicDescription = description.get(topicName);

            if (topicDescription == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Topic '" + topicName + "' not found."));
            }
            return ResponseEntity.ok(topicDescription);
        } catch (ExecutionException e) {
            if (e.getCause() != null && e.getCause().getClass().getSimpleName().equals("UnknownTopicOrPartitionException")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new MessageResponse("Topic '" + topicName + "' not found."));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Error describing topic: " + e.getMessage()));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Request interrupted."));
        }
    }

    @DeleteMapping("/topics/{topicName}")
    public ResponseEntity<MessageResponse> deleteTopic(@PathVariable String topicName) {
        try {
            kafkaService.deleteTopic(topicName);
            MessageResponse response = new MessageResponse("Topic " + topicName + " deleted successfully.");
            return ResponseEntity.ok(response);
        } catch (ExecutionException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
