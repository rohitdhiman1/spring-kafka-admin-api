import { useQuery } from '@tanstack/react-query';
import { RefreshCw, AlertCircle, CheckCircle, Server } from 'lucide-react';
import { kafkaApi } from '../services/kafkaApi';

const Cluster = () => {
  const { data: cluster, isLoading: clusterLoading, error: clusterError, refetch: refetchCluster } = useQuery({
    queryKey: ['cluster'],
    queryFn: () => kafkaApi.getClusterInfo(),
    refetchInterval: 30000,
  });

  const { data: health, isLoading: healthLoading, refetch: refetchHealth } = useQuery({
    queryKey: ['health'],
    queryFn: () => kafkaApi.getHealth(),
    refetchInterval: 30000,
  });

  const handleRefresh = () => {
    refetchCluster();
    refetchHealth();
  };

  const isHealthy = health?.status === 'UP';

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Cluster Information</h1>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            View cluster status and broker details
          </p>
        </div>
        <button
          onClick={handleRefresh}
          className="flex items-center px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors"
        >
          <RefreshCw className="w-4 h-4 mr-2" />
          Refresh
        </button>
      </div>

      {/* Health Status */}
      <div className={`rounded-lg shadow border p-6 ${
        isHealthy
          ? 'bg-green-50 dark:bg-green-900/20 border-green-200 dark:border-green-800'
          : 'bg-red-50 dark:bg-red-900/20 border-red-200 dark:border-red-800'
      }`}>
        <div className="flex items-center">
          {healthLoading ? (
            <>
              <div className="w-6 h-6 rounded-full bg-gray-300 dark:bg-gray-600 animate-pulse mr-3" />
              <span className="text-sm text-gray-600 dark:text-gray-400">Checking health...</span>
            </>
          ) : isHealthy ? (
            <>
              <CheckCircle className="w-6 h-6 text-green-600 dark:text-green-400 mr-3" />
              <div>
                <h3 className="text-sm font-medium text-green-800 dark:text-green-200">
                  Cluster is healthy
                </h3>
                <p className="text-sm text-green-700 dark:text-green-300">
                  All systems operational
                </p>
              </div>
            </>
          ) : (
            <>
              <AlertCircle className="w-6 h-6 text-red-600 dark:text-red-400 mr-3" />
              <div>
                <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                  Cluster health check failed
                </h3>
                <p className="text-sm text-red-700 dark:text-red-300">
                  Status: {health?.status || 'Unknown'}
                </p>
              </div>
            </>
          )}
        </div>
      </div>

      {/* Error Alert */}
      {clusterError && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <div className="flex items-start">
            <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400 mt-0.5 mr-3" />
            <div>
              <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                Error loading cluster information
              </h3>
              <p className="mt-1 text-sm text-red-700 dark:text-red-300">
                {clusterError instanceof Error ? clusterError.message : 'Unknown error occurred'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Cluster Details */}
      {cluster && (
        <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700 p-6">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white mb-4">
            Cluster Details
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
            <div className="p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
              <p className="text-sm text-gray-600 dark:text-gray-400">Cluster ID</p>
              <p className="mt-1 text-sm font-mono text-gray-900 dark:text-white break-all">
                {cluster.clusterId || 'N/A'}
              </p>
            </div>
            <div className="p-4 bg-gray-50 dark:bg-gray-700/50 rounded-lg">
              <p className="text-sm text-gray-600 dark:text-gray-400">Controller ID</p>
              <p className="mt-1 text-lg font-semibold text-gray-900 dark:text-white">
                {cluster.controller?.id ?? 'N/A'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Broker Nodes */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700">
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700">
          <h2 className="text-lg font-semibold text-gray-900 dark:text-white flex items-center">
            <Server className="w-5 h-5 mr-2" />
            Broker Nodes
          </h2>
        </div>
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-900">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Node ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Host
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Port
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Rack
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {clusterLoading ? (
                <tr>
                  <td colSpan={4} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    Loading broker information...
                  </td>
                </tr>
              ) : cluster?.nodes && cluster.nodes.length > 0 ? (
                cluster.nodes.map((node: any) => (
                  <tr key={node.id} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="flex items-center">
                        <div className="text-sm font-medium text-gray-900 dark:text-white">
                          {node.id}
                        </div>
                        {node.id === cluster.controller?.id && (
                          <span className="ml-2 px-2 py-0.5 text-xs bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-300 rounded">
                            Controller
                          </span>
                        )}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900 dark:text-white font-mono">
                        {node.host}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-900 dark:text-white font-mono">
                        {node.port}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm text-gray-500 dark:text-gray-400">
                        {node.rack || 'N/A'}
                      </div>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={4} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    No broker nodes found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default Cluster;
