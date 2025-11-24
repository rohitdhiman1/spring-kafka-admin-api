import { useQuery } from '@tanstack/react-query';
import { AlertCircle, Server, FileText, Users, RefreshCw } from 'lucide-react';
import { kafkaApi } from '../services/kafkaApi';

const Dashboard = () => {
  const { data: topics, isLoading: topicsLoading, refetch: refetchTopics } = useQuery({
    queryKey: ['topics'],
    queryFn: () => kafkaApi.listTopics(),
    refetchInterval: 30000, // Refresh every 30 seconds
  });

  const { data: consumerGroups, isLoading: groupsLoading, refetch: refetchGroups } = useQuery({
    queryKey: ['consumerGroups'],
    queryFn: () => kafkaApi.listConsumerGroups(),
    refetchInterval: 30000,
  });

  const { data: cluster, isLoading: clusterLoading, refetch: refetchCluster } = useQuery({
    queryKey: ['cluster'],
    queryFn: () => kafkaApi.getClusterInfo(),
    refetchInterval: 30000,
  });

  const { data: underReplicated, refetch: refetchUnderReplicated } = useQuery({
    queryKey: ['underReplicated'],
    queryFn: () => kafkaApi.getUnderReplicatedPartitions(),
    refetchInterval: 30000,
  });

  const handleRefreshAll = () => {
    refetchTopics();
    refetchGroups();
    refetchCluster();
    refetchUnderReplicated();
  };

  const stats = [
    {
      name: 'Total Topics',
      value: topics?.length ?? 0,
      icon: FileText,
      color: 'bg-blue-500',
      loading: topicsLoading,
    },
    {
      name: 'Consumer Groups',
      value: consumerGroups?.length ?? 0,
      icon: Users,
      color: 'bg-green-500',
      loading: groupsLoading,
    },
    {
      name: 'Cluster Nodes',
      value: cluster?.nodes?.length ?? 0,
      icon: Server,
      color: 'bg-purple-500',
      loading: clusterLoading,
    },
    {
      name: 'Under-Replicated',
      value: underReplicated?.length ?? 0,
      icon: AlertCircle,
      color: underReplicated && underReplicated.length > 0 ? 'bg-red-500' : 'bg-gray-400',
      loading: false,
    },
  ];

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Overview of your Kafka cluster
          </p>
        </div>
        <button
          onClick={handleRefreshAll}
          className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
        >
          <RefreshCw className="w-4 h-4 mr-2" />
          Refresh All
        </button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {stats.map((stat) => {
          const Icon = stat.icon;
          return (
            <div
              key={stat.name}
              className="bg-white dark:bg-gray-800 rounded-lg shadow p-6 border border-gray-200 dark:border-gray-700"
            >
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm font-medium text-gray-600 dark:text-gray-400">
                    {stat.name}
                  </p>
                  {stat.loading ? (
                    <div className="mt-2 h-8 w-16 bg-gray-200 dark:bg-gray-700 rounded animate-pulse" />
                  ) : (
                    <p className="mt-2 text-3xl font-semibold text-gray-900 dark:text-white">
                      {stat.value}
                    </p>
                  )}
                </div>
                <div className={`p-3 rounded-lg ${stat.color}`}>
                  <Icon className="w-6 h-6 text-white" />
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {/* Under-Replicated Partitions Alert */}
      {underReplicated && underReplicated.length > 0 && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <div className="flex items-start">
            <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400 mt-0.5 mr-3 flex-shrink-0" />
            <div>
              <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                Under-Replicated Partitions Detected
              </h3>
              <p className="mt-1 text-sm text-red-700 dark:text-red-300">
                {underReplicated.length} partition(s) are under-replicated. This may indicate issues with your brokers.
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Cluster Info */}
      {cluster && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700 p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Cluster Information
          </h2>
          <div className="space-y-3">
            <div className="flex justify-between">
              <span className="text-sm text-gray-600 dark:text-gray-400">Cluster ID:</span>
              <span className="text-sm font-mono text-gray-900 dark:text-white">
                {cluster.clusterId || 'N/A'}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-sm text-gray-600 dark:text-gray-400">Controller ID:</span>
              <span className="text-sm font-mono text-gray-900 dark:text-white">
                {cluster.controller?.id ?? 'N/A'}
              </span>
            </div>
            <div className="flex justify-between">
              <span className="text-sm text-gray-600 dark:text-gray-400">Total Nodes:</span>
              <span className="text-sm font-semibold text-gray-900 dark:text-white">
                {cluster.nodes?.length ?? 0}
              </span>
            </div>
          </div>
        </div>
      )}

      {/* Recent Topics */}
      {topics && topics.length > 0 && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700 p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Recent Topics
          </h2>
          <div className="space-y-2">
            {topics.slice(0, 5).map((topic: any) => (
              <div
                key={topic.name}
                className="flex items-center justify-between p-3 bg-gray-50 dark:bg-gray-700/50 rounded-lg"
              >
                <span className="text-sm font-medium text-gray-900 dark:text-white">
                  {topic.name}
                </span>
                <span className="text-xs px-2 py-1 bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300 rounded-full">
                  Active
                </span>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
};

export default Dashboard;
