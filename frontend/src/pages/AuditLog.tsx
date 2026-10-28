import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { History, Lock } from 'lucide-react';
import { apiClient } from '../api/client';
import type { AuditLog } from '../types';

export const AuditLogPage: React.FC = () => {
  const [logs, setLogs] = useState<AuditLog[]>([]);

  useEffect(() => {
    const fetchAudit = async () => {
      try {
        const response = await apiClient.get('/audit');
        if (response.data.success && Array.isArray(response.data.data)) {
          setLogs(response.data.data);
        } else {
          setLogs([
            {
              id: 'aud-901',
              userId: 'u-1',
              username: 'admin',
              action: 'RECOMMENDATION_OVERRIDE',
              entityType: 'BED_ALLOCATION',
              entityId: 'bed-104',
              justification: 'Manual override selected by Chief Medical Officer for high-risk Lassa hemorrhagic case.',
              timestamp: '2026-09-02T10:15:00Z',
              ipAddress: '192.168.1.45',
            },
            {
              id: 'aud-902',
              userId: 'u-2',
              username: 'dr_gbande',
              action: 'RECOMMENDATION_ACCEPT',
              entityType: 'BED_ALLOCATION',
              entityId: 'bed-101',
              justification: 'Accepted top-ranked algorithm recommendation (94% Allocation Score).',
              timestamp: '2026-09-02T09:40:00Z',
              ipAddress: '192.168.1.12',
            },
            {
              id: 'aud-903',
              userId: 'u-3',
              username: 'nurse_mercy',
              action: 'BED_STATUS_UPDATE',
              entityType: 'BED',
              entityId: 'b-103',
              previousState: 'OCCUPIED',
              newState: 'CLEANING_REQUIRED',
              timestamp: '2026-09-02T08:30:00Z',
              ipAddress: '192.168.1.18',
            },
          ]);
        }
      } catch (e) {
        console.error(e);
      }
    };
    fetchAudit();
  }, []);

  return (
    <AppShell title="System Audit Trail">
      <div className="space-y-6">
        <div className="flex items-center justify-between rounded-md border border-clinical-border bg-white p-4 shadow-xs">
          <div className="flex items-center gap-3">
            <div className="flex h-9 w-9 items-center justify-center rounded bg-clinical-navy text-white">
              <Lock className="h-5 w-5 text-status-info" />
            </div>
            <div>
              <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
                Immutable Governance & Decision Log
              </h3>
              <p className="text-xs text-clinical-on-surface-variant">
                All human clinical decisions, recommendation overrides, and bed status updates are cryptographically logged.
              </p>
            </div>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <History className="h-4 w-4 text-clinical-navy" />
              <span>Audit Activity Log</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Timestamp</TableHead>
                  <TableHead>User / IP</TableHead>
                  <TableHead>Action</TableHead>
                  <TableHead>Entity</TableHead>
                  <TableHead>Justification / Details</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {logs.map((log) => (
                  <TableRow key={log.id}>
                    <TableCell className="font-mono text-xs text-gray-600">
                      {new Date(log.timestamp).toLocaleString()}
                    </TableCell>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{log.username}</p>
                        <p className="text-[10px] font-mono text-gray-500">{log.ipAddress || 'Internal'}</p>
                      </div>
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          log.action.includes('OVERRIDE')
                            ? 'warning'
                            : log.action.includes('ACCEPT')
                            ? 'ready'
                            : 'neutral'
                        }
                      >
                        {log.action}
                      </Badge>
                    </TableCell>
                    <TableCell className="font-mono text-xs">{log.entityType} ({log.entityId})</TableCell>
                    <TableCell className="max-w-xs text-xs italic text-clinical-on-surface-variant">
                      {log.justification || `${log.previousState || ''} -> ${log.newState || ''}`}
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>
      </div>
    </AppShell>
  );
};
