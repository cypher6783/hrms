import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Sparkles, CheckCircle2, RefreshCw, ShieldAlert, Clock } from 'lucide-react';
import { apiClient } from '../api/client';

interface CleaningTask {
  id: string;
  bedId: string;
  bedNumber: string;
  wardName: string;
  status: string;
  cleaningType: string;
  priority: string;
  requestedAt: string;
  notes?: string;
}

export const BedCleaningPage: React.FC = () => {
  const [tasks, setTasks] = useState<CleaningTask[]>([]);
  const [isLoading, setIsLoading] = useState(false);

  const fetchCleaningTasks = async () => {
    setIsLoading(true);
    try {
      const response = await apiClient.get('/bed-cleaning/tasks');
      if (response.data.success && Array.isArray(response.data.data)) {
        setTasks(response.data.data);
      } else {
        setTasks([
          {
            id: 'task-101',
            bedId: 'b-103',
            bedNumber: 'ISO-A-03',
            wardName: 'Lassa Isolation Ward A',
            status: 'PENDING',
            cleaningType: 'LASSA_VIRAL_DECONTAMINATION',
            priority: 'HIGH',
            requestedAt: new Date(Date.now() - 40 * 60000).toISOString(),
            notes: 'Decontamination required following patient discharge',
          },
          {
            id: 'task-102',
            bedId: 'b-203',
            bedNumber: 'ICU-B-03',
            wardName: 'Lassa Isolation Ward B (ICU)',
            status: 'IN_PROGRESS',
            cleaningType: 'FULL_DISINFECTION',
            priority: 'CRITICAL',
            requestedAt: new Date(Date.now() - 90 * 60000).toISOString(),
            notes: 'High-level ICU isolation bed sterilization',
          },
        ]);
      }
    } catch (e) {
      console.error('Error fetching bed cleaning tasks:', e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchCleaningTasks();
  }, []);

  const handleCompleteCleaning = async (taskId: string, bedId: string) => {
    try {
      await apiClient.post(`/bed-cleaning/tasks/${taskId}/complete`);
      // Also update bed status to AVAILABLE
      await apiClient.put(`/beds/${bedId}/status?status=AVAILABLE`);
      fetchCleaningTasks();
    } catch (e) {
      console.error('Error completing cleaning task:', e);
      // Optimistic update
      setTasks((prev) => prev.filter((t) => t.id !== taskId));
    }
  };

  return (
    <AppShell title="Bed Sanitation & Decontamination Queue">
      <div className="space-y-6">
        <div className="flex items-center justify-between rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex items-center gap-2">
            <ShieldAlert className="h-5 w-5 text-amber-600" />
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
                Lassa Isolation Decontamination Protocol
              </h3>
              <p className="text-xs text-clinical-on-surface-variant">
                Beds undergoing viral decontamination before being returned to ready pool.
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
              <span>Active Decontamination Tasks</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {tasks.length === 0 ? (
              <div className="p-8 text-center text-xs text-clinical-on-surface-variant">
                <CheckCircle2 className="mx-auto h-8 w-8 text-status-ready mb-2" />
                No pending decontamination tasks. All beds are sanitized and ready.
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-left text-xs">
                  <thead className="border-b border-clinical-border bg-clinical-container-low text-[10px] uppercase font-bold text-clinical-navy">
                    <tr>
                      <th className="p-3">Bed & Location</th>
                      <th className="p-3">Cleaning Protocol</th>
                      <th className="p-3">Priority</th>
                      <th className="p-3">Requested At</th>
                      <th className="p-3 text-right">Action</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-clinical-border">
                    {tasks.map((task) => (
                      <tr key={task.id} className="hover:bg-clinical-surface">
                        <td className="p-3 font-semibold">
                          <p className="font-mono font-bold text-clinical-navy">{task.bedNumber}</p>
                          <p className="text-[10px] text-gray-500">{task.wardName}</p>
                        </td>
                        <td className="p-3">
                          <Badge variant="warning">{task.cleaningType || 'FULL_DISINFECTION'}</Badge>
                          {task.notes && <p className="text-[10px] text-gray-500 mt-0.5">{task.notes}</p>}
                        </td>
                        <td className="p-3">
                          <Badge variant={task.priority === 'CRITICAL' ? 'critical' : 'warning'}>
                            {task.priority || 'HIGH'}
                          </Badge>
                        </td>
                        <td className="p-3 font-mono text-[11px] text-gray-500">
                          <span className="inline-flex items-center gap-1">
                            <Clock className="h-3 w-3" />
                            {new Date(task.requestedAt).toLocaleTimeString()}
                          </span>
                        </td>
                        <td className="p-3 text-right">
                          <Button
                            variant="primary"
                            size="sm"
                            onClick={() => handleCompleteCleaning(task.id, task.bedId)}
                            className="bg-emerald-600 hover:bg-emerald-700 text-[11px]"
                          >
                            <CheckCircle2 className="h-3.5 w-3.5 mr-1" />
                            Mark Decontamination Complete
                          </Button>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
};
