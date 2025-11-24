import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { Plus, Trash2, RefreshCw, AlertCircle } from 'lucide-react';
import { kafkaApi } from '../services/kafkaApi';

interface CreateTopicForm {
  topicName: string;
  numPartitions: number;
  replicationFactor: number;
}

const Topics = () => {
  const queryClient = useQueryClient();
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [deleteConfirm, setDeleteConfirm] = useState<string | null>(null);
  const [formData, setFormData] = useState<CreateTopicForm>({
    topicName: '',
    numPartitions: 1,
    replicationFactor: 1,
  });

  const { data: topics, isLoading, error, refetch } = useQuery({
    queryKey: ['topics'],
    queryFn: () => kafkaApi.listTopics(),
    refetchInterval: 30000,
  });

  const createMutation = useMutation({
    mutationFn: (data: CreateTopicForm) => kafkaApi.createTopic(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['topics'] });
      setShowCreateModal(false);
      setFormData({ topicName: '', numPartitions: 1, replicationFactor: 1 });
    },
  });

  const deleteMutation = useMutation({
    mutationFn: (topicName: string) => kafkaApi.deleteTopic(topicName),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['topics'] });
      setDeleteConfirm(null);
    },
  });

  const handleCreateSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    createMutation.mutate(formData);
  };

  const handleDelete = (topicName: string) => {
    deleteMutation.mutate(topicName);
  };

  return (
    <div className="space-y-6">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-gray-900 dark:text-white">Topics</h1>
          <p className="mt-1 text-sm text-gray-500 dark:text-gray-400">
            Manage Kafka topics
          </p>
        </div>
        <div className="flex gap-3">
          <button
            onClick={() => refetch()}
            className="flex items-center px-4 py-2 bg-gray-200 dark:bg-gray-700 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-600 transition-colors"
          >
            <RefreshCw className="w-4 h-4 mr-2" />
            Refresh
          </button>
          <button
            onClick={() => setShowCreateModal(true)}
            className="flex items-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
          >
            <Plus className="w-4 h-4 mr-2" />
            Create Topic
          </button>
        </div>
      </div>

      {/* Error Alert */}
      {error && (
        <div className="bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg p-4">
          <div className="flex items-start">
            <AlertCircle className="w-5 h-5 text-red-600 dark:text-red-400 mt-0.5 mr-3" />
            <div>
              <h3 className="text-sm font-medium text-red-800 dark:text-red-200">
                Error loading topics
              </h3>
              <p className="mt-1 text-sm text-red-700 dark:text-red-300">
                {error instanceof Error ? error.message : 'Unknown error occurred'}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Topics Table */}
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow border border-gray-200 dark:border-gray-700 overflow-hidden">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-200 dark:divide-gray-700">
            <thead className="bg-gray-50 dark:bg-gray-900">
              <tr>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Topic Name
                </th>
                <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Status
                </th>
                <th className="px-6 py-3 text-right text-xs font-medium text-gray-500 dark:text-gray-400 uppercase tracking-wider">
                  Actions
                </th>
              </tr>
            </thead>
            <tbody className="bg-white dark:bg-gray-800 divide-y divide-gray-200 dark:divide-gray-700">
              {isLoading ? (
                <tr>
                  <td colSpan={3} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    Loading topics...
                  </td>
                </tr>
              ) : topics && topics.length > 0 ? (
                topics.map((topic: any) => (
                  <tr key={topic.name} className="hover:bg-gray-50 dark:hover:bg-gray-700/50">
                    <td className="px-6 py-4 whitespace-nowrap">
                      <div className="text-sm font-medium text-gray-900 dark:text-white">
                        {topic.name}
                      </div>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap">
                      <span className="px-2 py-1 text-xs font-medium bg-green-100 dark:bg-green-900/30 text-green-800 dark:text-green-300 rounded-full">
                        Active
                      </span>
                    </td>
                    <td className="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                      {deleteConfirm === topic.name ? (
                        <div className="flex items-center justify-end gap-2">
                          <span className="text-sm text-gray-600 dark:text-gray-400">
                            Confirm delete?
                          </span>
                          <button
                            onClick={() => handleDelete(topic.name)}
                            className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700"
                          >
                            Yes
                          </button>
                          <button
                            onClick={() => setDeleteConfirm(null)}
                            className="px-3 py-1 bg-gray-200 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded hover:bg-gray-300 dark:hover:bg-gray-500"
                          >
                            No
                          </button>
                        </div>
                      ) : (
                        <button
                          onClick={() => setDeleteConfirm(topic.name)}
                          className="text-red-600 hover:text-red-900 dark:text-red-400 dark:hover:text-red-300"
                        >
                          <Trash2 className="w-4 h-4 inline" />
                        </button>
                      )}
                    </td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan={3} className="px-6 py-4 text-center text-sm text-gray-500 dark:text-gray-400">
                    No topics found
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Create Topic Modal */}
      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black bg-opacity-50">
          <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-md w-full p-6">
            <h2 className="text-xl font-bold text-gray-900 dark:text-white mb-4">
              Create New Topic
            </h2>
            <form onSubmit={handleCreateSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Topic Name
                </label>
                <input
                  type="text"
                  required
                  value={formData.topicName}
                  onChange={(e) => setFormData({ ...formData, topicName: e.target.value })}
                  className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                  placeholder="my-topic"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Partitions
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  value={formData.numPartitions}
                  onChange={(e) => setFormData({ ...formData, numPartitions: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Replication Factor
                </label>
                <input
                  type="number"
                  required
                  min="1"
                  value={formData.replicationFactor}
                  onChange={(e) => setFormData({ ...formData, replicationFactor: parseInt(e.target.value) })}
                  className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                />
              </div>
              {createMutation.isError && (
                <div className="text-sm text-red-600 dark:text-red-400">
                  Error: {createMutation.error instanceof Error ? createMutation.error.message : 'Failed to create topic'}
                </div>
              )}
              <div className="flex justify-end gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => setShowCreateModal(false)}
                  className="px-4 py-2 bg-gray-200 dark:bg-gray-600 text-gray-700 dark:text-gray-200 rounded-lg hover:bg-gray-300 dark:hover:bg-gray-500"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  disabled={createMutation.isPending}
                  className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 disabled:opacity-50"
                >
                  {createMutation.isPending ? 'Creating...' : 'Create'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default Topics;
