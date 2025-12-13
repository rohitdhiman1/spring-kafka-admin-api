package net.rohitdhiman.springkafkaadminapi.dto;

public class NodeInfo {
    private final int id;
    private final String host;
    private final int port;
    private final String rack;

    public NodeInfo(int id, String host, int port, String rack) {
        this.id = id;
        this.host = host;
        this.port = port;
        this.rack = rack;
    }

    public NodeInfo(org.apache.kafka.common.Node node) {
        this(node.id(), node.host(), node.port(), node.rack());
    }

    public int getId() {
        return id;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getRack() {
        return rack;
    }
}
