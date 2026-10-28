import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Sparkles, CheckCircle2, RefreshCw, ShieldAlert, Clock, UserCheck, Play, CheckCheck, AlertCircle } from 'lucide-react';
import { apiClient } from '../api/client';
import { useAuth } from '../auth/AuthContext';

interface CleaningTask {
  id: string;
  bedId: string;
  bedNumber?: string;
  wardName?: string;
  admissionId?: string;
  status: 'PENDING' | 'ASSIGNED' | 'IN_PROGRESS' | 'COMPLETED' | 'VERIFIED' | string;
  assignedTo?: string;
  assignedAt?: string;
  startedAt?: string;
  completedAt?: string;
  verifiedBy?: string;
  verifiedAt?: string;
  cleaningNotes?: string;
  cleaningType?: string;
  priority?: string;
  requestedAt?: string;
  createdAt?: string;
}

export const BedCleaningPage: React.FC = () => {
  const { user } = useAuth();
  const [tasks, setTasks] = useState<CleaningTask[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [actionLoadingId, setActionLoadingId] = useState<string | null>(null);
  const [feedbackMsg, setFeedbackMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Complete Notes Modal State
  const [isCompleteModalOpen, setIsCompleteModalOpen] = useState(false);
  const [selectedTaskId, setSelectedTaskId] = useState<string | null>(null);
  const [cleaningNotes, setCleaningNotes] = useState('');

  const fetchCleaningTasks = async () => {
    setIsLoading(true);
    setFeedbackMsg(null);
    try {
      const response = await apiClient.get('/bed-cleaning');
      if (response.data.success) {
        const data = response.data.data;
        const taskList: CleaningTask[] = data?.content || (Array.isArray(data) ? data : []);
        setTasks(taskList);
      }
    } catch (e: any) {
      console.error('Error fetching bed cleaning tasks:', e);
      setFeedbackMsg({ type: 'error', text: 'Failed to retrieve decontamination tasks from server.' });
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCleaningTasks();
  }, []);

  const handleAssignTask = async (taskId: string) => {
    setActionLoadingId(taskId);
    setFeedbackMsg(null);
    try {
      const staffId = user?.userId || '00000000-0000-0000-0000-000000000000';
      await apiClient.post(`/bed-cleaning/${taskId}/assign`, {
        staffId: staffId,
      });
      setFeedbackMsg({ type: 'success', text: 'Task successfully assigned to staff.' });
      fetchCleaningTasks();
    } catch (e: any) {
      console.error('Error assigning cleaning task:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Failed to assign decontamination task.' });
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleStartCleaning = async (taskId: string) => {
    setActionLoadingId(taskId);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/bed-cleaning/${taskId}/start`);
      setFeedbackMsg({ type: 'success', text: 'Decontamination process started (IN_PROGRESS).' });
      fetchCleaningTasks();
    } catch (e: any) {
      console.error('Error starting cleaning task:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Failed to start decontamination task.' });
    } finally {
      setActionLoadingId(null);
    }
  };

  const openCompleteModal = (taskId: string) => {
    setSelectedTaskId(taskId);
    setCleaningNotes('Standard hypochlorite chemical decontamination protocol verified.');
    setIsCompleteModalOpen(true);
  };

  const handleCompleteSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedTaskId) return;

    setActionLoadingId(selectedTaskId);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/bed-cleaning/${selectedTaskId}/complete`, {
        cleaningNotes: cleaningNotes.trim(),
      });
      setIsCompleteModalOpen(false);
      setFeedbackMsg({ type: 'success', text: 'Decontamination completed. Bed awaiting clinical verification.' });
      fetchCleaningTasks();
    } catch (e: any) {
      console.error('Error completing cleaning task:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Failed to complete decontamination task.' });
    } finally {
      setActionLoadingId(null);
    }
  };

  const handleVerifyCleaning = async (taskId: string) => {
    setActionLoadingId(taskId);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/bed-cleaning/${taskId}/verify`);
      setFeedbackMsg({
        type: 'success',
        text: 'Decontamination verified! Bed has been officially returned to the AVAILABLE pool.',
      });
      fetchCleaningTasks();
    } catch (e: any) {
      console.error('Error verifying cleaning task:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Failed to verify decontamination task.' });
    } finally {
      setActionLoadingId(null);
    }
  };

  const getStatusBadge = (status: string) => {
    switch (status) {
      case 'PENDING':
        return <Badge variant="neutral">Pending Assignment</Badge>;
      case 'ASSIGNED':
        return <Badge variant="info">Assigned</Badge>;
      case 'IN_PROGRESS':
        return <Badge variant="warning">In Progress</Badge>;
      case 'COMPLETED':
        return <Badge variant="info">Awaiting Verification</Badge>;
      case 'VERIFIED':
        return <Badge variant="ready">Verified & Available</Badge>;
      default:
        return <Badge variant="neutral">{status}</Badge>;
    }
  };

  return (
    <AppShell title="Bed Sanitation & Decontamination Queue">
      <div className="space-y-6">
        {feedbackMsg && (
          <div
            className={`flex items-center gap-2 rounded-md p-3 text-xs font-semibold ${
              feedbackMsg.type === 'success'
                ? 'border border-emerald-300 bg-emerald-50 text-emerald-800'
                : 'border border-red-300 bg-red-50 text-red-800'
            }`}
          >
            {feedbackMsg.type === 'success' ? (
              <CheckCircle2 className="h-4 w-4 shrink-0 text-emerald-600" />
            ) : (
              <AlertCircle className="h-4 w-4 shrink-0 text-red-600" />
            )}
            <span>{feedbackMsg.text}</span>
          </div>
        )}

        <div className="flex items-center justify-between rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex items-center gap-2">
            <ShieldAlert className="h-5 w-5 text-amber-600" />
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
                Lassa Isolation Decontamination Protocol
              </h3>
              <p className="text-xs text-clinical-on-surface-variant">
                Beds undergoing mandatory 4-stage viral decontamination (Assign → Start → Complete → Verify) before returning to the ready pool.
              </p>
            </div>
          </div>
          <Button variant="outline" size="sm" onClick={fetchCleaningTasks} isLoading={isLoading}>
            <RefreshCw className="h-3.5 w-3.5 mr-1" />
            Refresh Queue
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Sparkles className="h-4 w-4 text-amber-500" />
              <span>Decontamination Queue & Verification Pipeline</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {tasks.length === 0 ? (
              <div className="p-8 text-center text-xs text-clinical-on-surface-variant">
                <CheckCircle2 className="mx-auto h-8 w-8 text-status-ready mb-2" />
                No active decontamination tasks in queue. All hospital isolation beds are sanitary.
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-clinical-border bg-clinical-container-low text-[10px] uppercase font-bold text-clinical-navy">
                    <tr>
                      <th className="p-3">Bed Identifier</th>
                      <th className="p-3">Status</th>
                      <th className="p-3">Notes / Protocol</th>
                      <th className="p-3">Logged Timestamp</th>
                      <th className="p-3 text-right">Mandatory Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-clinical-border">
                    {tasks.map((task) => (
                      <tr key={task.id} className="hover:bg-clinical-surface">
                        <td className="p-3 font-semibold">
                          <p className="font-mono font-bold text-clinical-navy">
                            {task.bedNumber || `Bed: ${task.bedId.substring(0, 8)}...`}
                          </p>
                          <p className="text-[10px] text-gray-500">{task.wardName || 'Isolation Ward'}</p>
                        </td>
                        <td className="p-3">{getStatusBadge(task.status)}</td>
                        <td className="p-3 max-w-xs text-clinical-on-surface-variant">
                          {task.cleaningNotes || '0.5% Sodium Hypochlorite decontamination protocol'}
                        </td>
                        <td className="p-3 font-mono text-[11px] text-gray-500">
                          <span className="inline-flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {new Date(task.createdAt || task.requestedAt || Date.now()).toLocaleTimeString()}
                          </span>
                        </td>
                        <td className="p-3 text-right space-x-1">
                          {task.status === 'PENDING' && (
                            <Button
                              variant="outline"
                              size="sm"
                              isLoading={actionLoadingId === task.id}
                              onClick={() => handleAssignTask(task.id)}
                              className="text-[11px]"
                            >
                              <UserCheck className="h-3 w-3 mr-1 text-clinical-navy" />
                              Assign Staff
                            </Button>
                          )}
                          {task.status === 'ASSIGNED' && (
                            <Button
                              variant="primary"
                              size="sm"
                              isLoading={actionLoadingId === task.id}
                              onClick={() => handleStartCleaning(task.id)}
                              className="bg-amber-600 hover:bg-amber-700 text-[11px]"
                            >
                              <Play className="h-3 w-3 mr-1" />
                              Start Decontamination
                            </Button>
                          )}
                          {task.status === 'IN_PROGRESS' && (
                            <Button
                              variant="primary"
                              size="sm"
                              isLoading={actionLoadingId === task.id}
                              onClick={() => openCompleteModal(task.id)}
                              className="bg-blue-600 hover:bg-blue-700 text-[11px]"
                            >
                              <CheckCircle2 className="h-3 w-3 mr-1" />
                              Mark Completed
                            </Button>
                          )}
                          {task.status === 'COMPLETED' && (
                            <Button
                              variant="primary"
                              size="sm"
                              isLoading={actionLoadingId === task.id}
                              onClick={() => handleVerifyCleaning(task.id)}
                              className="bg-emerald-600 hover:bg-emerald-700 text-[11px]"
                            >
                              <CheckCheck className="h-3.5 w-3.5 mr-1" />
                              Verify & Release Bed
                            </Button>
                          )}
                          {task.status === 'VERIFIED' && (
                            <span className="text-xs text-status-ready font-bold inline-flex items-center gap-1">
                              <CheckCircle2 className="h-4 w-4 text-status-ready" />
                              Released
                            </span>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </CardContent>
        </Card>

        {/* Modal: Complete Cleaning Task */}
        <Modal
          isOpen={isCompleteModalOpen}
          onClose={() => setIsCompleteModalOpen(false)}
          title="Complete Bed Decontamination Protocol"
        >
          <form onSubmit={handleCompleteSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-bold text-clinical-navy mb-1">
                Decontamination Notes & Chemical Verification
              </label>
              <textarea
                value={cleaningNotes}
                onChange={(e) => setCleaningNotes(e.target.value)}
                rows={3}
                required
                className="w-full rounded border border-clinical-border p-2 text-xs focus:outline-none focus:ring-1 focus:ring-clinical-navy"
                placeholder="Detail the disinfection solution, contact time, and mattress inspection..."
              />
            </div>
            <div className="rounded border border-amber-200 bg-amber-50 p-2 text-[11px] text-amber-800">
              Note: Submitting completion marks this bed as <strong>COMPLETED</strong>. It will remain unavailable to clinical allocation until officially <strong>VERIFIED</strong> by infection control.
            </div>
            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" size="sm" onClick={() => setIsCompleteModalOpen(false)}>
                Cancel
              </Button>
              <Button type="submit" variant="primary" size="sm" isLoading={actionLoadingId === selectedTaskId}>
                Submit Completion
              </Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
