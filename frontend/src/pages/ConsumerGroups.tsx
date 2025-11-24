import { useQuery } from '@tanstack/react-query';
import { RefreshCw, AlertCircle } from 'lucide-react';
import { kafkaApi } from '../services/kafkaApi';

const ConsumerGroups = () => {
  const { data: groups, isLoading, error, refetch } = useQuery({
    queryKey: ['consumerGroups'],
    queryFn: () => kafkaApi.listConsumerGroups(),
    refetchInterval: 30000,
  });

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Consumer Groups</h1>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Monitor consumer group status and lag
          </p>
        </div>
        <button
          onClick={() => refetch()}
          className="flex items-center px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors"
        >
          <RefreshCw className="w-4 h-4 mr-2" />
          Refresh
        </button>
      </div>

      {/* Error Alert */}
      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <div className="flex items-start">
            <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400 mt-0.5 mr-3" />
            <div>
              <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                Error loading consumer groups
              </h3>
              <p className="mt-1 text-sm text-red-700 dark:text-red-300">
                {error instanceof Error ? error.message : 'Unknown error occurred'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Consumer Groups Table */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-900">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Group ID
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  State
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {isLoading ? (
                <tr>
                  <td colSpan={2} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    Loading consumer groups...
                  </td>
                </tr>
              ) : groups && groups.length > 0 ? (
                groups.map((group: any) => (
                  <tr key={group.groupId} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900 dark:text-white">
                        {group.groupId}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span
                        className={`px-2 py-1 text-xs font-medium rounded-full ${
                          group.state === 'Stable'
                            ? 'bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300'
                            : group.state === 'Empty'
                            ? 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-800 dark:text-yellow-300'
                            : 'bg-gray-100 dark:bg-gray-700 text-gray-800 dark:text-gray-300'
                        }`}
                      >
                        {group.state}
                      </span>
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={2} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    No consumer groups found
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

export default ConsumerGroups;
