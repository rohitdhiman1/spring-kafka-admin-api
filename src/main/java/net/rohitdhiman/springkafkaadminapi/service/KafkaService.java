package net.rohitdhiman.springkafkaadminapi.service;

import net.rohitdhiman.springkafkaadminapi.exception.TopicAlreadyExistsException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.kafka.common.errors.TopicExistsException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class KafkaService {

    private final AdminClient adminClient;

    public KafkaService(AdminClient adminClient) {
        this.adminClient = adminClient;
    }

    public void createTopic(String topicName, Integer numPartitions, Short replicationFactor) throws ExecutionException, InterruptedException {
        int finalNumPartitions = (numPartitions != null) ? numPartitions : 1;
        short finalReplicationFactor = (replicationFactor != null) ? replicationFactor : 1;
        NewTopic newTopic = new NewTopic(topicName, finalNumPartitions, finalReplicationFactor);
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

    public net.rohitdhiman.springkafkaadminapi.dto.ClusterInfo describeCluster() throws ExecutionException, InterruptedException {
        DescribeClusterResult clusterResult = adminClient.describeCluster();
        String clusterId = clusterResult.clusterId().get();
        Node controller = clusterResult.controller().get();
        Collection<Node> nodes = clusterResult.nodes().get();
        
        // Convert Node objects to NodeInfo DTOs
        net.rohitdhiman.springkafkaadminapi.dto.NodeInfo controllerInfo = 
            controller != null ? new net.rohitdhiman.springkafkaadminapi.dto.NodeInfo(controller) : null;
        List<net.rohitdhiman.springkafkaadminapi.dto.NodeInfo> nodeInfoList = nodes.stream()
            .map(net.rohitdhiman.springkafkaadminapi.dto.NodeInfo::new)
            .collect(Collectors.toList());
        
        return new net.rohitdhiman.springkafkaadminapi.dto.ClusterInfo(clusterId, controllerInfo, nodeInfoList);
    }

    public Collection<ConsumerGroupListing> listConsumerGroups() throws ExecutionException, InterruptedException {
        return adminClient.listConsumerGroups().all().get();
    }

    public Map<String, ConsumerGroupDescription> describeConsumerGroups(Collection<String> groupIds) throws ExecutionException, InterruptedException {
        return adminClient.describeConsumerGroups(groupIds).all().get();
    }

    public Map<String, List<Integer>> findUnderReplicatedPartitions() throws ExecutionException, InterruptedException {
        Collection<String> allTopicNames = adminClient.listTopics().names().get();
        Map<String, TopicDescription> topicDescriptions = adminClient.describeTopics(allTopicNames).allTopicNames().get();

        return topicDescriptions.values().stream()
                .filter(description -> description.partitions().stream()
                        .anyMatch(partitionInfo -> partitionInfo.isr().size() < partitionInfo.replicas().size()))
                .collect(Collectors.toMap(TopicDescription::name,
                        description -> description.partitions().stream()
                                .filter(partitionInfo -> partitionInfo.isr().size() < partitionInfo.replicas().size())
                                .map(TopicPartitionInfo::partition)
                                .collect(Collectors.toList())));
    }
}
