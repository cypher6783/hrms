import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { ShieldAlert, RefreshCw, CheckCircle2 } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Bed } from '../types';

export const BedsWards: React.FC = () => {
  const [beds, setBeds] = useState<Bed[]>([]);
  const [selectedWard, setSelectedWard] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');

  const fetchBeds = async () => {
    try {
      const response = await apiClient.get('/beds/filter');
      if (response.data.success && Array.isArray(response.data.data)) {
        setBeds(response.data.data);
      } else {
        setBeds([
          { id: 'b-101', bedNumber: 'ISO-A-01', wardId: 'w-1', wardName: 'Lassa Isolation Ward A', bedType: 'ISOLATION_NEGATIVE_PRESSURE', isIsolationCapable: true, status: 'AVAILABLE' },
          { id: 'b-102', bedNumber: 'ISO-A-02', wardId: 'w-1', wardName: 'Lassa Isolation Ward A', bedType: 'ISOLATION_NEGATIVE_PRESSURE', isIsolationCapable: true, status: 'OCCUPIED', currentPatientName: 'Terna Akura' },
          { id: 'b-103', bedNumber: 'ISO-A-03', wardId: 'w-1', wardName: 'Lassa Isolation Ward A', bedType: 'ISOLATION_NEGATIVE_PRESSURE', isIsolationCapable: true, status: 'CLEANING_REQUIRED' },
          { id: 'b-104', bedNumber: 'ISO-A-04', wardId: 'w-1', wardName: 'Lassa Isolation Ward A', bedType: 'ISOLATION_NEGATIVE_PRESSURE', isIsolationCapable: true, status: 'UNDER_CLEANING' },
          { id: 'b-201', bedNumber: 'ICU-B-01', wardId: 'w-2', wardName: 'Lassa Isolation Ward B (ICU)', bedType: 'ICU', isIsolationCapable: true, status: 'OCCUPIED', currentPatientName: 'Dooshima Orban' },
          { id: 'b-202', bedNumber: 'ICU-B-02', wardId: 'w-2', wardName: 'Lassa Isolation Ward B (ICU)', bedType: 'ICU', isIsolationCapable: true, status: 'AVAILABLE' },
          { id: 'b-301', bedNumber: 'HDU-01', wardId: 'w-3', wardName: 'High Dependency Unit (HDU)', bedType: 'HIGH_DEPENDENCY', isIsolationCapable: false, status: 'AVAILABLE' },
          { id: 'b-302', bedNumber: 'HDU-02', wardId: 'w-3', wardName: 'High Dependency Unit (HDU)', bedType: 'HIGH_DEPENDENCY', isIsolationCapable: false, status: 'MAINTENANCE' },
        ]);
      }
    } catch (e) {
      console.error('Error fetching beds:', e);
    }
  };

  useEffect(() => {
    fetchBeds();
  }, []);

  const filteredBeds = beds.filter((b) => {
    if (selectedWard !== 'ALL' && b.wardId !== selectedWard) return false;
    if (selectedStatus !== 'ALL' && b.status !== selectedStatus) return false;
    return true;
  });

  return (
    <AppShell title="Bed & Ward Management">
      <div className="space-y-6">
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex flex-wrap items-center gap-3">
            <div>
              <label className="block text-[10px] font-bold uppercase text-clinical-navy mb-1">Ward</label>
              <select
                value={selectedWard}
                onChange={(e) => setSelectedWard(e.target.value)}
                className="rounded border border-clinical-border bg-white py-1 px-2 text-xs font-semibold"
              >
                <option value="ALL">All Wards</option>
                <option value="w-1">Lassa Isolation Ward A</option>
                <option value="w-2">Lassa Isolation Ward B (ICU)</option>
                <option value="w-3">High Dependency Unit (HDU)</option>
              </select>
            </div>

            <div>
              <label className="block text-[10px] font-bold uppercase text-clinical-navy mb-1">Status Filter</label>
              <select
                value={selectedStatus}
                onChange={(e) => setSelectedStatus(e.target.value)}
                className="rounded border border-clinical-border bg-white py-1 px-2 text-xs font-semibold"
              >
                <option value="ALL">All Bed Statuses</option>
                <option value="AVAILABLE">Ready / Available</option>
                <option value="OCCUPIED">Occupied</option>
                <option value="CLEANING_REQUIRED">Decontamination Needed</option>
                <option value="UNDER_CLEANING">Decontamination Active</option>
                <option value="MAINTENANCE">Maintenance</option>
              </select>
            </div>
          </div>

          <Button variant="outline" size="sm" onClick={fetchBeds}>
            <RefreshCw className="h-3.5 w-3.5 mr-1" />
            Refresh Bed Matrix
          </Button>
        </div>

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {filteredBeds.map((bed) => (
            <Card
              key={bed.id}
              statusBarColor={
                bed.status === 'AVAILABLE'
                  ? 'ready'
                  : bed.status === 'OCCUPIED'
                  ? 'info'
                  : bed.status === 'CLEANING_REQUIRED' || bed.status === 'UNDER_CLEANING'
                  ? 'warning'
                  : 'critical'
              }
            >
              <CardContent className="space-y-3">
                <div className="flex items-start justify-between">
                  <div>
                    <h4 className="text-sm font-bold text-clinical-navy font-mono">{bed.bedNumber}</h4>
                    <p className="text-[10px] font-medium text-gray-500">{bed.wardName}</p>
                  </div>
                  <Badge
                    variant={
                      bed.status === 'AVAILABLE'
                        ? 'ready'
                        : bed.status === 'OCCUPIED'
                        ? 'info'
                        : bed.status === 'CLEANING_REQUIRED' || bed.status === 'UNDER_CLEANING'
                        ? 'warning'
                        : 'critical'
                    }
                  >
                    {bed.status}
                  </Badge>
                </div>

                <div className="rounded bg-clinical-container-low p-2 text-xs">
                  {bed.status === 'OCCUPIED' ? (
                    <div>
                      <p className="text-[10px] uppercase font-bold text-gray-400">Current Occupant</p>
                      <p className="font-bold text-clinical-navy">{bed.currentPatientName || 'Admitted Patient'}</p>
                    </div>
                  ) : (
                    <div>
                      <p className="text-[10px] uppercase font-bold text-gray-400">Bed Capability</p>
                      <p className="font-semibold text-clinical-on-surface">{bed.bedType}</p>
                    </div>
                  )}
                </div>

                <div className="flex items-center justify-between text-[11px] text-gray-500 pt-1">
                  {bed.isIsolationCapable && (
                    <span className="flex items-center gap-1 font-bold text-purple-700">
                      <ShieldAlert className="h-3 w-3 text-purple-600" /> Negative Pressure
                    </span>
                  )}
                  {bed.status === 'AVAILABLE' && (
                    <span className="flex items-center gap-1 text-status-ready font-semibold">
                      <CheckCircle2 className="h-3 w-3" /> Ready
                    </span>
                  )}
                </div>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    </AppShell>
  );
};
