package net.rohitdhiman.springkafkaadminapi.dto;

import org.apache.kafka.common.Node;

import java.util.Collection;

public record ClusterInfo(
        String clusterId,
        Node controller,
        Collection<Node> nodes
) {}
