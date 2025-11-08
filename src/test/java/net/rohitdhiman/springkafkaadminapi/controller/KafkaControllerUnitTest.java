package net.rohitdhiman.springkafkaadminapi.controller;

import net.rohitdhiman.springkafkaadminapi.exception.TopicAlreadyExistsException;
import net.rohitdhiman.springkafkaadminapi.service.KafkaService;
import org.apache.kafka.clients.admin.ListTopicsResult;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for KafkaController.
 * Uses MockMvc with standalone setup for lightweight testing of the web layer with mocked service.
 * These tests run fast without starting the full Spring context or any Kafka infrastructure.
 */
@ExtendWith(MockitoExtension.class)
class KafkaControllerUnitTest {

    private MockMvc mockMvc;

    @Mock
    private KafkaService kafkaService;

    @BeforeEach
    void setUp() {
        KafkaController controller = new KafkaController(kafkaService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testDescribeCluster_Success() throws Exception {
        // Arrange
        Node node1 = new Node(1, "localhost", 9092);
        Node node2 = new Node(2, "localhost", 9093);
        Collection<Node> nodes = Arrays.asList(node1, node2);
        when(kafkaService.describeCluster()).thenReturn(nodes);

        // Act & Assert
        mockMvc.perform(get("/api/cluster"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void testDescribeCluster_Error() throws Exception {
        // Arrange
        when(kafkaService.describeCluster()).thenThrow(new ExecutionException(new RuntimeException("Kafka error")));

        // Act & Assert
        mockMvc.perform(get("/api/cluster"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testListTopics_Success() throws Exception {
        // Arrange
        ListTopicsResult listTopicsResult = mock(ListTopicsResult.class);
        @SuppressWarnings("unchecked")
        KafkaFuture<Set<String>> future = mock(KafkaFuture.class);
        Set<String> topics = Set.of("topic1", "topic2", "topic3");
        
        when(kafkaService.listTopics()).thenReturn(listTopicsResult);
        when(listTopicsResult.names()).thenReturn(future);
        when(future.get()).thenReturn(topics);

        // Act & Assert - Just verify it returns OK and service is called
        mockMvc.perform(get("/api/topics"))
                .andExpect(status().isOk());
        
        verify(kafkaService, times(1)).listTopics();
    }

    @Test
    void testCreateTopic_Success() throws Exception {
        // Arrange
        doNothing().when(kafkaService).createTopic(anyString(), anyInt(), anyShort());

        // Act & Assert
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topicName\": \"new-topic\", \"numPartitions\": 3, \"replicationFactor\": 1}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Topic 'new-topic' created successfully."));
    }

    @Test
    void testCreateTopic_AlreadyExists() throws Exception {
        // Arrange
        doThrow(new TopicAlreadyExistsException("Topic 'existing-topic' already exists."))
                .when(kafkaService).createTopic(anyString(), anyInt(), anyShort());

        // Act & Assert
        mockMvc.perform(post("/api/topics")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"topicName\": \"existing-topic\", \"numPartitions\": 1, \"replicationFactor\": 1}"))
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteTopic_Success() throws Exception {
        // Arrange
        doNothing().when(kafkaService).deleteTopic(anyString());

        // Act & Assert
        mockMvc.perform(delete("/api/topics/topic-to-delete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Topic topic-to-delete deleted successfully."));
    }

    @Test
    void testGetUnderReplicatedPartitions_Success() throws Exception {
        // Arrange
        Map<String, List<Integer>> underReplicated = Map.of(
                "topic1", List.of(0, 2),
                "topic2", List.of(1)
        );
        when(kafkaService.findUnderReplicatedPartitions()).thenReturn(underReplicated);

        // Act & Assert
        mockMvc.perform(get("/api/topics/under-replicated"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.topic1").isArray())
                .andExpect(jsonPath("$.topic1.length()").value(2))
                .andExpect(jsonPath("$.topic2").isArray())
                .andExpect(jsonPath("$.topic2.length()").value(1));
    }
}
