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
  ArrowRight,
  ShieldCheck,
  PackageCheck
} from 'lucide-react';
import { apiClient } from '../api/client';
import type { AdmissionStats, WardStatus, Admission, Bed } from '../types';
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
      const [statsRes, wardsRes, bedsRes, admissionsRes] = await Promise.allSettled([
        apiClient.get('/admissions/stats'),
        apiClient.get('/wards'),
        apiClient.get('/beds/filter'),
        apiClient.get('/admissions'),
      ]);

      let bedsList: Bed[] = [];
      if (bedsRes.status === 'fulfilled' && bedsRes.value.data.success && Array.isArray(bedsRes.value.data.data)) {
        bedsList = bedsRes.value.data.data;
      }

      // 1. Calculate Real Dynamic Wards Occupancy from Bed Matrix
      if (wardsRes.status === 'fulfilled' && wardsRes.value.data.success) {
        const rawWards = Array.isArray(wardsRes.value.data.data) ? wardsRes.value.data.data : [];
        
        const calculatedWards: WardStatus[] = rawWards.map((w: any) => {
          const wardBeds = bedsList.filter((b) => b.wardId === w.id);
          const totalBeds = wardBeds.length > 0 ? wardBeds.length : (w.maxBedCapacity || 10);
          const occupiedBeds = wardBeds.filter((b) => b.status === 'OCCUPIED').length;
          const availableBeds = wardBeds.filter((b) => b.status === 'AVAILABLE').length;
          const cleaningBeds = wardBeds.filter(
            (b) => b.status === 'CLEANING_REQUIRED' || b.status === 'UNDER_CLEANING' || (b.status as string) === 'DIRTY'
          ).length;
          const maintenanceBeds = wardBeds.filter((b) => b.status === 'MAINTENANCE').length;
          const occupancyPercentage = totalBeds > 0 ? Math.round((occupiedBeds / totalBeds) * 100) : 0;

          return {
            wardId: w.id,
            wardName: w.name,
            totalBeds,
            occupiedBeds,
            availableBeds,
            cleaningBeds,
            maintenanceBeds,
            occupancyPercentage,
          };
        });

        setWards(calculatedWards);

        // Calculate Overall ICU and Isolation Occupancy Percentages dynamically
        const icuBeds = bedsList.filter((b) => b.bedType === 'ICU');
        const icuOccupied = icuBeds.filter((b) => b.status === 'OCCUPIED').length;
        const realIcuRate = icuBeds.length > 0 ? Math.round((icuOccupied / icuBeds.length) * 100) : 0;

        const isoBeds = bedsList.filter((b) => b.isIsolationCapable);
        const isoOccupied = isoBeds.filter((b) => b.status === 'OCCUPIED').length;
        const realIsoRate = isoBeds.length > 0 ? Math.round((isoOccupied / isoBeds.length) * 100) : 0;

        if (statsRes.status === 'fulfilled' && statsRes.value.data.success) {
          const apiStats = statsRes.value.data.data;
          setStats({
            totalAdmissions: (apiStats.activeAdmissions || 0) + (apiStats.dischargedToday || 0),
            pendingAllocations: apiStats.activeAdmissions || 0,
            activeAdmissions: apiStats.activeAdmissions || 0,
            todayDischarges: apiStats.dischargedToday || 0,
            icuOccupancyRate: realIcuRate,
            isolationOccupancyRate: realIsoRate,
          });
        }
      }

      // 2. Load Real Active Admissions
      if (admissionsRes.status === 'fulfilled' && admissionsRes.value.data.success) {
        const rawData = admissionsRes.value.data.data;
        const rawContent: Admission[] = Array.isArray(rawData?.content)
          ? rawData.content
          : Array.isArray(rawData)
          ? rawData
          : [];
        const content = rawContent.filter((adm: any) => adm.status !== 'DISCHARGED' && adm.isActive !== false);
        setPendingAdmissions(content);
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
        {/* Queue Pressure Alert */}
        {Array.isArray(pendingAdmissions) && pendingAdmissions.length > 0 && (
          <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-status-critical-border bg-status-critical-bg p-4 shadow-xs">
            <div className="flex items-center gap-3">
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-status-critical text-white">
                <AlertTriangle className="h-5 w-5" />
              </div>
              <div>
                <h4 className="text-xs font-bold uppercase tracking-wider text-status-critical">
                  Active Patient Admissions Queue — {pendingAdmissions.length} Registered Admission(s)
                </h4>
                <p className="text-xs text-clinical-on-surface-variant">
                  High-priority Lassa isolation cases ready for AI resource allocation.
                </p>
              </div>
            </div>
            <Button
              variant="danger"
              size="sm"
              onClick={() => navigate(`/recommendations?admissionId=${pendingAdmissions[0]?.id}`)}
              className="flex items-center gap-1 text-xs uppercase"
            >
              <span>Process Recommendation ({pendingAdmissions[0]?.admissionNumber})</span>
              <ArrowRight className="h-3.5 w-3.5" />
            </Button>
          </div>
        )}

        {/* Dynamic Metric Cards */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          <Card statusBarColor="info">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Active Admissions
                </p>
                <p className="text-3xl font-bold font-tabular text-clinical-navy mt-1">
                  {stats?.activeAdmissions ?? 0}
                </p>
                <span className="text-[10px] text-gray-500 font-medium">Currently in ward care</span>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-status-info-bg text-status-info">
                <Users className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor="warning">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  ICU Bed Occupancy Rate
                </p>
                <p className="text-3xl font-bold font-tabular text-status-warning mt-1">
                  {stats?.icuOccupancyRate !== undefined ? `${stats.icuOccupancyRate}%` : '0%'}
                </p>
                <span className="text-[10px] text-gray-500 font-medium">Calculated from ICU bed matrix</span>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-status-warning-bg text-status-warning">
                <BedDouble className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor="critical">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Lassa Isolation Occupancy
                </p>
                <p className="text-3xl font-bold font-tabular text-status-critical mt-1">
                  {stats?.isolationOccupancyRate !== undefined ? `${stats.isolationOccupancyRate}%` : '0%'}
                </p>
                <span className="text-[10px] text-gray-500 font-medium">Negative pressure units</span>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-status-critical-bg text-status-critical">
                <ShieldCheck className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>

          <Card statusBarColor="ready">
            <CardContent className="flex items-center justify-between">
              <div>
                <p className="text-[11px] font-bold uppercase tracking-wider text-clinical-on-surface-variant">
                  Discharges Today
                </p>
                <p className="text-3xl font-bold font-tabular text-status-ready mt-1">
                  {stats?.todayDischarges ?? 0}
                </p>
                <span className="text-[10px] text-gray-500 font-medium">Decontamination pending</span>
              </div>
              <div className="flex h-12 w-12 items-center justify-center rounded-full bg-status-ready-bg text-status-ready">
                <PackageCheck className="h-6 w-6" />
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Real Wards Capacity Matrix */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span className="flex items-center gap-2">
                <BedDouble className="h-4 w-4 text-clinical-navy" />
                Real Ward Capacity & Bed Availability Matrix
              </span>
              <Button variant="outline" size="sm" onClick={() => navigate('/beds')}>
                Manage Beds & Wards
              </Button>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Ward Name</TableHead>
                  <TableHead>Total Beds</TableHead>
                  <TableHead>Occupied</TableHead>
                  <TableHead>Available</TableHead>
                  <TableHead>Decontamination</TableHead>
                  <TableHead>Maintenance</TableHead>
                  <TableHead>Occupancy %</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {Array.isArray(wards) && wards.map((w) => (
                  <TableRow key={w.wardId}>
                    <TableCell className="font-bold text-clinical-navy">{w.wardName}</TableCell>
                    <TableCell className="font-mono">{w.totalBeds}</TableCell>
                    <TableCell className="font-mono text-status-info font-semibold">{w.occupiedBeds}</TableCell>
                    <TableCell className="font-mono text-status-ready font-bold">{w.availableBeds}</TableCell>
                    <TableCell className="font-mono text-amber-600 font-semibold">{w.cleaningBeds}</TableCell>
                    <TableCell className="font-mono text-gray-500">{w.maintenanceBeds}</TableCell>
                    <TableCell>
                      <div className="flex items-center gap-2">
                        <div className="h-2 w-20 rounded-full bg-gray-200 overflow-hidden">
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
                        <span className="font-mono text-xs font-bold">{w.occupancyPercentage}%</span>
                      </div>
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>

        {/* Active Admissions Pending Recommendations */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center justify-between">
              <span className="flex items-center gap-2">
                <Sparkles className="h-4 w-4 text-status-info" />
                Active Admissions Pending AI Allocation Recommendation
              </span>
              <Button variant="outline" size="sm" onClick={() => navigate('/patients')}>
                View All Admissions
              </Button>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            {!Array.isArray(pendingAdmissions) || pendingAdmissions.length === 0 ? (
              <div className="p-6 text-center text-xs text-clinical-on-surface-variant">
                <CheckCircle2 className="mx-auto h-8 w-8 text-status-ready mb-2" />
                All active admissions have completed resource allocation decisions.
              </div>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Admission Number</TableHead>
                    <TableHead>Patient ID</TableHead>
                    <TableHead>Ward / Bed ID</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead className="text-right">Action</TableHead>
                  </TableRow>
                </TableHeader>
                <tbody>
                  {Array.isArray(pendingAdmissions) && pendingAdmissions.map((adm) => (
                    <TableRow key={adm.id}>
                      <TableCell className="font-bold text-clinical-navy font-mono">
                        {adm.admissionNumber}
                      </TableCell>
                      <TableCell className="font-mono text-xs">{adm.patientId}</TableCell>
                      <TableCell className="text-xs">{adm.wardId || 'Isolation Ward A'}</TableCell>
                      <TableCell>
                        <Badge variant="info">{adm.status}</Badge>
                      </TableCell>
                      <TableCell className="text-right">
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => navigate(`/recommendations?admissionId=${adm.id}`)}
                          className="text-xs bg-status-info hover:bg-blue-700"
                        >
                          <Sparkles className="h-3.5 w-3.5 mr-1" />
                          Process Recommendation
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
    </AppShell>
  );
};
