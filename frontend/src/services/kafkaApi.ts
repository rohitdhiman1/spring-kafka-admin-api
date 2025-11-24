import axios from 'axios';

const API_BASE_URL = '/api';

export interface Topic {
  name: string;
  status: string;
  message: string;
  _links?: {
    self: { href: string };
    delete: { href: string };
  };
}

export interface TopicDescription {
  name: string;
  partitions: Array<{
    partition: number;
    leader: number;
    replicas: number[];
    isr: number[];
  }>;
}

export interface ConsumerGroup {
  groupId: string;
  isSimpleConsumerGroup: boolean;
  state?: string;
}

export interface ClusterNode {
  id: number;
  host: string;
  port: number;
  rack?: string;
}

export interface CreateTopicRequest {
  topicName: string;
  numPartitions?: number;
  replicationFactor?: number;
}

class KafkaApiService {
  // Topics
  async listTopics() {
    const response = await axios.get(`${API_BASE_URL}/topics`);
    return response.data.content || [];
  }

  async getTopic(name: string): Promise<TopicDescription> {
    const response = await axios.get(`${API_BASE_URL}/topics/${name}`);
    return response.data;
  }

  async createTopic(data: CreateTopicRequest) {
    const response = await axios.post(`${API_BASE_URL}/topics`, data);
    return response.data;
  }

  async deleteTopic(name: string) {
    const response = await axios.delete(`${API_BASE_URL}/topics/${name}`);
    return response.data;
  }

  async getUnderReplicatedPartitions() {
    const response = await axios.get(`${API_BASE_URL}/topics/under-replicated`);
    return response.data;
  }

  // Consumer Groups
  async listConsumerGroups() {
    const response = await axios.get(`${API_BASE_URL}/consumer-groups`);
    return response.data || [];
  }

  async getConsumerGroup(groupId: string) {
    const response = await axios.get(`${API_BASE_URL}/consumer-groups/${groupId}`);
    return response.data;
  }

  // Cluster
  async getClusterInfo() {
    const response = await axios.get(`${API_BASE_URL}/cluster`);
    return response.data;
  }

  // Health
  async getHealth() {
    const response = await axios.get(`/actuator/health`);
    return response.data;
  }
}

export const kafkaApi = new KafkaApiService();
