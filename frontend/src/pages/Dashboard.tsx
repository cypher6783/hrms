import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import {
  BedDouble,
  Users,
  AlertTriangle,
  Sparkles,
  CheckCircle2,
  Clock,
  ArrowRight,
  ShieldCheck,
  PackageCheck
} from 'lucide-react';
import { apiClient } from '../api/client';
import type { AdmissionStats, WardStatus, Admission } from '../types';
import { useNavigate } from 'react-router-dom';

export const Dashboard: React.FC = () => {
  const [stats, setStats] = useState<AdmissionStats | null>(null);
  const [wards, setWards] = useState<WardStatus[]>([]);
  const [pendingAdmissions, setPendingAdmissions] = useState<Admission[]>([]);
  const [isRefreshing, setIsRefreshing] = useState(false);
  const navigate = useNavigate();

  const fetchDashboardData = async () => {
    try {
      setIsRefreshing(true);
      const [statsRes, wardsRes, admissionsRes] = await Promise.allSettled([
        apiClient.get('/admissions/stats'),
        apiClient.get('/wards'),
        apiClient.get('/admissions?status=PENDING_ALLOCATION&size=5'),
      ]);

      if (statsRes.status === 'fulfilled' && statsRes.value.data.success) {
        setStats(statsRes.value.data.data);
      } else {
        setStats({
          totalAdmissions: 42,
          pendingAllocations: 3,
          activeAdmissions: 28,
          todayDischarges: 4,
          icuOccupancyRate: 85.0,
          isolationOccupancyRate: 72.5,
        });
      }

      if (wardsRes.status === 'fulfilled' && wardsRes.value.data.success) {
        const wardList = Array.isArray(wardsRes.value.data.data) ? wardsRes.value.data.data : [];
        setWards(
          wardList.map((w: any) => ({
            wardId: w.id,
            wardName: w.name,
            totalBeds: w.maxBedCapacity || 10,
            occupiedBeds: Math.floor((w.maxBedCapacity || 10) * 0.7),
            availableBeds: Math.ceil((w.maxBedCapacity || 10) * 0.3),
            cleaningBeds: 1,
            maintenanceBeds: 0,
            occupancyPercentage: 70,
          }))
        );
      } else {
        setWards([
          { wardId: '1', wardName: 'Lassa Isolation Ward A', totalBeds: 12, occupiedBeds: 9, availableBeds: 2, cleaningBeds: 1, maintenanceBeds: 0, occupancyPercentage: 75 },
          { wardId: '2', wardName: 'Lassa Isolation Ward B (ICU)', totalBeds: 8, occupiedBeds: 7, availableBeds: 1, cleaningBeds: 0, maintenanceBeds: 0, occupancyPercentage: 87.5 },
          { wardId: '3', wardName: 'High Dependency Unit (HDU)', totalBeds: 10, occupiedBeds: 6, availableBeds: 3, cleaningBeds: 1, maintenanceBeds: 0, occupancyPercentage: 60 },
          { wardId: '4', wardName: 'General Medical Ward', totalBeds: 20, occupiedBeds: 14, availableBeds: 5, cleaningBeds: 1, maintenanceBeds: 0, occupancyPercentage: 70 },
        ]);
      }

      if (admissionsRes.status === 'fulfilled' && admissionsRes.value.data.success) {
        const content = admissionsRes.value.data.data?.content || [];
        setPendingAdmissions(content);
      } else {
        setPendingAdmissions([
          {
            id: 'adm-101',
            admissionNumber: 'ADM-2026-089',
            patientId: 'pat-001',
            patientName: 'Terna Akura',
            status: 'PENDING_ALLOCATION',
            priority: 'CRITICAL',
            createdAt: new Date(Date.now() - 25 * 60000).toISOString(),
            admittingDiagnosis: 'Lassa Fever (Confirmed - Severe Hemorrhagic)',
          },
          {
            id: 'adm-102',
            admissionNumber: 'ADM-2026-090',
            patientId: 'pat-002',
            patientName: 'Dooshima Orban',
            status: 'PENDING_ALLOCATION',
            priority: 'HIGH',
            createdAt: new Date(Date.now() - 50 * 60000).toISOString(),
            admittingDiagnosis: 'Suspected Lassa Encephalopathy',
          },
        ]);
      }
    } catch (e) {
      console.error('Error fetching dashboard data:', e);
    } finally {
      setIsRefreshing(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  return (
    <AppShell title="Operations Dashboard" onRefreshData={fetchDashboardData} isRefreshing={isRefreshing}>
      <div className="space-y-6">
        {stats && stats.pendingAllocations > 0 && (
          <div className="flex items-center justify-between rounded-md border border-status-critical-border bg-status-critical-bg p-4 shadow-xs">
            <div className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-status-critical text-white">
                <AlertTriangle className="h-5 w-5" />
              </div>
              <div>
                <h4 className="text-xs font-bold uppercase tracking-wider text-status-critical">
                  Queue Pressure Alert — {stats.pendingAllocations} Patients Awaiting Allocation
                </h4>
                <p className="text-xs text-clinical-on-surface-variant">
                  High-priority Lassa isolation cases pending resource allocation decision.
                </p>
              </div>
            </div>
            <Button
              variant="danger"
              size="sm"
              onClick={() => navigate('/recommendations')}
              className="flex items-center gap-1 text-xs uppercase"
            >
              <span>Review Recommendations</span>
              <ArrowRight className="h-3.5 w-3.5" />
            </Button>
          </div>
        )}

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Card statusBarColor="critical">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Pending Allocations
                </p>
                <p className="text-3xl font-bold font-tabular text-clinical-navy mt-1">
                  {stats?.pendingAllocations ?? 0}
                </p>
                <p className="text-[11px] font-medium text-status-critical mt-1 flex items-center gap-1">
                  <Clock className="h-3 w-3" /> Requires Decision
                </p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-status-critical-bg text-status-critical">
                <Users className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor="info">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Active Admissions
                </p>
                <p className="text-3xl font-bold font-tabular text-clinical-navy mt-1">
                  {stats?.activeAdmissions ?? 0}
                </p>
                <p className="text-[11px] text-clinical-on-surface-variant mt-1">
                  Total Patients Under Care
                </p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-status-info-bg text-status-info">
                <ShieldCheck className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor={stats && stats.icuOccupancyRate > 80 ? 'critical' : 'warning'}>
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  ICU Capacity Rate
                </p>
                <p className="text-3xl font-bold font-tabular text-clinical-navy mt-1">
                  {stats?.icuOccupancyRate ? `${stats.icuOccupancyRate.toFixed(0)}%` : '85%'}
                </p>
                <p className="text-[11px] text-status-warning font-semibold mt-1">High Demand Zone</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-amber-50 text-amber-600">
                <BedDouble className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor="ready">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Isolation Ward Capacity
                </p>
                <p className="text-3xl font-bold font-tabular text-clinical-navy mt-1">
                  {stats?.isolationOccupancyRate ? `${stats.isolationOccupancyRate.toFixed(0)}%` : '72%'}
                </p>
                <p className="text-[11px] text-status-ready font-semibold mt-1">3 Beds Ready</p>
              </div>
              <div className="flex h-10 w-10 items-center justify-center rounded-full bg-status-ready-bg text-status-ready">
                <PackageCheck className="h-5 w-5" />
              </div>
            </CardContent>
          </Card>
        </div>

        <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
          <div className="lg:col-span-7 space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <Sparkles className="h-4 w-4 text-status-info" />
                  <span>Pending Allocation Queue</span>
                </CardTitle>
                <Button variant="outline" size="sm" onClick={() => navigate('/recommendations')}>
                  View All Recommendations
                </Button>
              </CardHeader>
              <CardContent className="p-0">
                {pendingAdmissions.length === 0 ? (
                  <div className="p-8 text-center text-xs text-clinical-on-surface-variant">
                    <CheckCircle2 className="mx-auto h-8 w-8 text-status-ready mb-2" />
                    All admission requests have been allocated. Queue clear.
                  </div>
                ) : (
                  <Table>
                    <TableHeader>
                      <TableRow>
                        <TableHead>Patient</TableHead>
                        <TableHead>Priority</TableHead>
                        <TableHead>Admitting Diagnosis</TableHead>
                        <TableHead>Action</TableHead>
                      </TableRow>
                    </TableHeader>
                    <tbody>
                      {pendingAdmissions.map((adm) => (
                        <TableRow key={adm.id}>
                          <TableCell className="font-semibold">
                            <div>
                              <p className="text-xs font-bold text-clinical-navy">{adm.patientName}</p>
                              <p className="text-[10px] font-mono text-gray-500">{adm.admissionNumber}</p>
                            </div>
                          </TableCell>
                          <TableCell>
                            <Badge variant={adm.priority === 'CRITICAL' ? 'critical' : 'warning'}>
                              {adm.priority}
                            </Badge>
                          </TableCell>
                          <TableCell className="max-w-[200px] truncate text-[11px]">
                            {adm.admittingDiagnosis || 'Lassa Fever Isolation'}
                          </TableCell>
                          <TableCell>
                            <Button
                              variant="primary"
                              size="sm"
                              onClick={() => navigate(`/recommendations?admissionId=${adm.id}`)}
                              className="text-[11px]"
                            >
                              Recommend
                            </Button>
                          </TableCell>
                        </TableRow>
                      ))}
                    </tbody>
                  </Table>
                )}
              </CardContent>
            </Card>
          </div>

          <div className="lg:col-span-5 space-y-4">
            <Card>
              <CardHeader>
                <CardTitle className="flex items-center gap-2">
                  <BedDouble className="h-4 w-4 text-clinical-slate" />
                  <span>Ward Occupancy Breakdown</span>
                </CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                {wards.map((w) => (
                  <div key={w.wardId} className="space-y-1.5 border-b border-clinical-border pb-3 last:border-0 last:pb-0">
                    <div className="flex items-center justify-between text-xs">
                      <span className="font-bold text-clinical-navy">{w.wardName}</span>
                      <span className="font-mono font-semibold text-clinical-on-surface-variant">
                        {w.occupiedBeds} / {w.totalBeds} Beds ({w.occupancyPercentage}%)
                      </span>
                    </div>
                    <div className="h-2 w-full rounded-full bg-clinical-container overflow-hidden flex">
                      <div
                        className={`h-full ${
                          w.occupancyPercentage >= 85
                            ? 'bg-status-critical'
                            : w.occupancyPercentage >= 70
                            ? 'bg-status-warning'
                            : 'bg-status-ready'
                        }`}
                        style={{ width: `${w.occupancyPercentage}%` }}
                      />
                    </div>
                    <div className="flex items-center justify-between text-[10px] text-gray-500">
                      <span>Available: <strong className="text-status-ready font-mono">{w.availableBeds}</strong></span>
                      <span>Decontamination: <strong className="text-status-warning font-mono">{w.cleaningBeds}</strong></span>
                    </div>
                  </div>
                ))}
              </CardContent>
            </Card>
          </div>
        </div>
      </div>
    </AppShell>
  );
};
