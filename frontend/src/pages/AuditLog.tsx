import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { History, Lock, RefreshCw, Filter } from 'lucide-react';
import { apiClient } from '../api/client';
import type { AuditLog } from '../types';

export const AuditLogPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [actionFilter, setActionFilter] = useState('');

  const fetchAudit = async () => {
    setIsLoading(true);
    try {
      const url = actionFilter ? `/audit?actionType=${encodeURIComponent(actionFilter)}` : '/audit';
      const response = await apiClient.get(url);
      if (response.data.success) {
        const payload = response.data.data;
        const list: AuditLog[] = payload?.content || (Array.isArray(payload) ? payload : []);
        setLogs(list);
      }
    } catch (e) {
      console.error('Failed to load audit logs:', e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    fetchAudit();
  }, [actionFilter]);

  const getActionBadgeVariant = (action: string) => {
    const act = (action || '').toUpperCase();
    if (act.includes('OVERRIDE')) return 'warning';
    if (act.includes('ACCEPT') || act.includes('VERIFIED')) return 'ready';
    if (act.includes('REJECT') || act.includes('DELETE')) return 'critical';
    return 'neutral';
  };

  return (
    <AppShell title="System Audit Trail">
      <div className="space-y-6">
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-4 shadow-xs">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded bg-clinical-navy text-white">
              <Lock className="h-5 w-5 text-status-info" />
            </div>
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
                Immutable Governance & Clinical Audit Trail
              </h3>
              <p className="text-xs text-clinical-on-surface-variant">
                Every clinical bed allocation, algorithm recommendation decision, override, and sanitation transition is cryptographically verified and recorded.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-2">
            <div className="flex items-center gap-1.5 text-xs">
              <Filter className="h-3.5 w-3.5 text-gray-500" />
              <select
                value={actionFilter}
                onChange={(e) => setActionFilter(e.target.value)}
                className="rounded border border-clinical-border bg-white px-2 py-1 text-xs font-mono font-medium text-clinical-navy focus:outline-none"
              >
                <option value="">All Actions</option>
                <option value="RECOMMENDATION_ACCEPT">Recommendation Accept</option>
                <option value="RECOMMENDATION_OVERRIDE">Recommendation Override</option>
                <option value="RECOMMENDATION_REJECT">Recommendation Reject</option>
                <option value="BED_CLEANING">Bed Sanitation</option>
              </select>
            </div>

            <Button variant="outline" size="sm" onClick={fetchAudit} isLoading={isLoading}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh
            </Button>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <History className="h-4 w-4 text-clinical-navy" />
              <span>Immutable Activity Log ({logs.length} Records)</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {logs.length === 0 ? (
              <div className="p-8 text-center text-xs text-clinical-on-surface-variant">
                No audit log entries found matching criteria.
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Timestamp</TableHead>
                    <TableHead>User / IP</TableHead>
                    <TableHead>Action</TableHead>
                    <TableHead>Entity</TableHead>
                    <TableHead>Justification / State Transition</TableHead>
                  </TableRow>
                </TableHeader>
                <tbody>
                  {logs.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell className="font-mono text-xs text-gray-600">
                        {log.timestamp ? new Date(log.timestamp).toLocaleString() : 'Just now'}
                      </TableCell>
                      <TableCell>
                        <div>
                          <p className="font-bold text-clinical-navy">{log.username || 'Clinical Staff'}</p>
                          <p className="text-[10px] font-mono text-gray-500">{log.ipAddress || 'Internal Network'}</p>
                        </div>
                      </TableCell>
                      <TableCell>
                        <Badge variant={getActionBadgeVariant(log.action)}>
                          {log.action}
                        </Badge>
                      </TableCell>
                      <TableCell className="font-mono text-xs">
                        <span className="font-bold text-clinical-navy">{log.entityType}</span>
                        {log.entityId && (
                          <span className="block text-[10px] text-gray-500">
                            {log.entityId.substring(0, 8)}...
                          </span>
                        )}
                      </TableCell>
                      <TableCell className="max-w-xs text-xs italic text-clinical-on-surface-variant">
                        {log.justification ||
                          (log.previousState && log.newState
                            ? `${log.previousState} → ${log.newState}`
                            : 'Logged event')}
                      </TableCell>
                    </TableRow>
                  ))}
                </tbody>
              </Table>
            )}
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
};
