package net.rohitdhiman.springkafkaadminapi.dto;

import java.util.List;

public record ClusterInfo(
        String clusterId,
        NodeInfo controller,
        List<NodeInfo> nodes
) {}
