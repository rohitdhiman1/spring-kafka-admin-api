package net.rohitdhiman.springkafkaadminapi.service;

import net.rohitdhiman.springkafkaadminapi.exception.TopicAlreadyExistsException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaService {

    private final AdminClient adminClient;

    public KafkaService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public void createTopic(String topicName) throws ExecutionException, InterruptedException {
        int numPartitions = 1;
        short replicationFactor = 1;
        NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
        CreateTopicsResult createTopicsResult = adminClient.createTopics(Collections.singleton(newTopic));
        try {
            createTopicsResult.all().get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof TopicExistsException) {
                throw new TopicAlreadyExistsException("Topic '" + topicName + "' already exists.");
            }
            throw e;
        }
    }

    public ListTopicsResult listTopics() {
        return adminClient.listTopics();
    }

    // New method to describe topics.
    public Map<String, TopicDescription> describeTopics(Collection<String> topics) throws ExecutionException, InterruptedException {
        DescribeTopicsResult result = adminClient.describeTopics(topics);
        return result.allTopicNames().get();
    }

    // New method to delete a topic.
    public void deleteTopic(String topicName) throws ExecutionException, InterruptedException {
        adminClient.deleteTopics(Collections.singleton(topicName)).all().get();
    }
}
