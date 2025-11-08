package net.rohitdhiman.springkafkaadminapi.service;

import net.rohitdhiman.springkafkaadminapi.exception.TopicAlreadyExistsException;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.errors.TopicExistsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.*;

/**
 * Unit tests for KafkaService.
 * These tests use mocked dependencies (AdminClient) and run fast without any infrastructure.
 * Use these tests during development for quick feedback on business logic.
 */
@ExtendWith(MockitoExtension.class)
class KafkaServiceUnitTest {

    @Mock
    private AdminClient adminClient;

    private KafkaService kafkaService;

    @BeforeEach
    void setUp() {
        kafkaService = new KafkaService(adminClient);
    }

    @Test
    void testCreateTopic_Success() throws ExecutionException, InterruptedException {
        // Arrange
        CreateTopicsResult createTopicsResult = mock(CreateTopicsResult.class);
        KafkaFuture<Void> future = KafkaFuture.completedFuture(null);
        
        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(future);

        // Act
        kafkaService.createTopic("test-topic", 3, (short) 1);

        // Assert
        verify(adminClient, times(1)).createTopics(any());
    }

    @Test
    void testCreateTopic_AlreadyExists() throws ExecutionException, InterruptedException {
        // Arrange
        CreateTopicsResult createTopicsResult = mock(CreateTopicsResult.class);
        @SuppressWarnings("unchecked")
        KafkaFuture<Void> future = mock(KafkaFuture.class);
        
        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(future);
        when(future.get()).thenThrow(new ExecutionException(new TopicExistsException("Topic exists")));

        // Act & Assert
        assertThrows(TopicAlreadyExistsException.class, () -> {
            kafkaService.createTopic("existing-topic", 1, (short) 1);
        });
    }

    @Test
    void testCreateTopic_WithDefaults() throws ExecutionException, InterruptedException {
        // Arrange
        CreateTopicsResult createTopicsResult = mock(CreateTopicsResult.class);
        KafkaFuture<Void> future = KafkaFuture.completedFuture(null);
        
        when(adminClient.createTopics(any())).thenReturn(createTopicsResult);
        when(createTopicsResult.all()).thenReturn(future);

        // Act - passing null for partitions and replication factor should use defaults
        kafkaService.createTopic("default-topic", null, null);

        // Assert
        verify(adminClient, times(1)).createTopics(any());
    }

    @Test
    void testListTopics() {
        // Arrange
        ListTopicsResult listTopicsResult = mock(ListTopicsResult.class);
        when(adminClient.listTopics()).thenReturn(listTopicsResult);

        // Act
        ListTopicsResult result = kafkaService.listTopics();

        // Assert
        assertNotNull(result);
        verify(adminClient, times(1)).listTopics();
    }

    @Test
    void testDescribeCluster() throws ExecutionException, InterruptedException {
        // Arrange
        DescribeClusterResult describeClusterResult = mock(DescribeClusterResult.class);
        @SuppressWarnings("unchecked")
        KafkaFuture<Collection<Node>> future = mock(KafkaFuture.class);
        Node node1 = new Node(1, "localhost", 9092);
        Node node2 = new Node(2, "localhost", 9093);
        Collection<Node> nodes = Arrays.asList(node1, node2);
        
        when(adminClient.describeCluster()).thenReturn(describeClusterResult);
        when(describeClusterResult.nodes()).thenReturn(future);
        when(future.get()).thenReturn(nodes);

        // Act
        Collection<Node> result = kafkaService.describeCluster();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.contains(node1));
        assertTrue(result.contains(node2));
    }

    @Test
    void testDeleteTopic() throws ExecutionException, InterruptedException {
        // Arrange
        DeleteTopicsResult deleteTopicsResult = mock(DeleteTopicsResult.class);
        KafkaFuture<Void> future = KafkaFuture.completedFuture(null);
        
        when(adminClient.deleteTopics(anyCollection())).thenReturn(deleteTopicsResult);
        when(deleteTopicsResult.all()).thenReturn(future);

        // Act
        kafkaService.deleteTopic("topic-to-delete");

        // Assert
        verify(adminClient, times(1)).deleteTopics(anyCollection());
    }

    @Test
    void testListConsumerGroups() throws ExecutionException, InterruptedException {
        // Arrange
        ListConsumerGroupsResult listConsumerGroupsResult = mock(ListConsumerGroupsResult.class);
        @SuppressWarnings("unchecked")
        KafkaFuture<Collection<ConsumerGroupListing>> future = mock(KafkaFuture.class);
        Collection<ConsumerGroupListing> groups = Collections.emptyList();
        
        when(adminClient.listConsumerGroups()).thenReturn(listConsumerGroupsResult);
        when(listConsumerGroupsResult.all()).thenReturn(future);
        when(future.get()).thenReturn(groups);

        // Act
        Collection<ConsumerGroupListing> result = kafkaService.listConsumerGroups();

        // Assert
        assertNotNull(result);
        verify(adminClient, times(1)).listConsumerGroups();
    }

    @Test
    void testDescribeTopics() throws ExecutionException, InterruptedException {
        // Arrange
        DescribeTopicsResult describeTopicsResult = mock(DescribeTopicsResult.class);
        @SuppressWarnings("unchecked")
        KafkaFuture<Map<String, TopicDescription>> future = mock(KafkaFuture.class);
        Map<String, TopicDescription> descriptions = new HashMap<>();
        
        when(adminClient.describeTopics(anyCollection())).thenReturn(describeTopicsResult);
        when(describeTopicsResult.allTopicNames()).thenReturn(future);
        when(future.get()).thenReturn(descriptions);

        // Act
        Map<String, TopicDescription> result = kafkaService.describeTopics(Collections.singletonList("test-topic"));

        // Assert
        assertNotNull(result);
        verify(adminClient, times(1)).describeTopics(anyCollection());
    }
}
