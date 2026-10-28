import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { ShieldAlert, RefreshCw, CheckCircle2, Plus } from 'lucide-react';
import { apiClient } from '../api/client';
import { useAuth } from '../auth/AuthContext';
import type { Bed } from '../types';

export const BedsWards: React.FC = () => {
  const { user } = useAuth();
  const userRole = (user?.role || '').toUpperCase();
  const isAdmin = userRole === 'ADMINISTRATOR' || userRole === 'ADMIN';
  const isWardManager = userRole === 'WARD_MANAGER';

  const [beds, setBeds] = useState<Bed[]>([]);
  const [wardsList, setWardsList] = useState<Array<{ id: string; name: string }>>([]);
  const [selectedWard, setSelectedWard] = useState<string>('ALL');
  const [selectedStatus, setSelectedStatus] = useState<string>('ALL');

  // Modals
  const [isBedModalOpen, setIsBedModalOpen] = useState(false);
  const [isWardModalOpen, setIsWardModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);

  // Forms
  const [bedForm, setBedForm] = useState({
    bedNumber: '',
    wardId: '',
    bedType: 'ISOLATION_NEGATIVE_PRESSURE',
    isIsolationCapable: true,
  });

  const [wardForm, setWardForm] = useState({
    name: '',
    maxBedCapacity: 10,
    isolationLevel: 'HIGH_ISOLATION',
    equipmentZone: 'ZONE_A',
  });

  const fetchWards = async () => {
    try {
      const res = await apiClient.get('/wards');
      if (res.data.success && Array.isArray(res.data.data)) {
        setWardsList(res.data.data);
        if (res.data.data.length > 0 && !bedForm.wardId) {
          setBedForm((prev) => ({ ...prev, wardId: res.data.data[0].id }));
        }
      }
    } catch (e) {
      console.error('Error fetching wards:', e);
    }
  };

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
    fetchWards();
    fetchBeds();
  }, []);

  const handleCreateBed = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await apiClient.post('/beds', bedForm);
      setIsBedModalOpen(false);
      fetchBeds();
    } catch (err) {
      console.error('Error creating bed:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCreateWard = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await apiClient.post('/wards', wardForm);
      setIsWardModalOpen(false);
      fetchWards();
      fetchBeds();
    } catch (err) {
      console.error('Error creating ward:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUpdateBedStatus = async (bedId: string, newStatus: string) => {
    try {
      await apiClient.put(`/beds/${bedId}/status?status=${newStatus}`);
      fetchBeds();
    } catch (err) {
      console.error('Error updating bed status:', err);
      setBeds((prev) => prev.map((b) => (b.id === bedId ? { ...b, status: newStatus as any } : b)));
    }
  };

  const filteredBeds = beds.filter((b) => {
    if (selectedWard !== 'ALL' && b.wardId !== selectedWard) return false;
    if (selectedStatus !== 'ALL' && b.status !== selectedStatus) return false;
    return true;
  });

  return (
    <AppShell title="Bed & Ward Management">
      <div className="space-y-6">
        {/* Top Control Bar */}
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
                {wardsList.map((w) => (
                  <option key={w.id} value={w.id}>{w.name}</option>
                ))}
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
                <option value="DIRTY">Dirty / Decontamination Needed</option>
                <option value="CLEANING_REQUIRED">Decontamination Needed</option>
                <option value="UNDER_CLEANING">Decontamination Active</option>
                <option value="MAINTENANCE">Maintenance</option>
              </select>
            </div>
          </div>

          <div className="flex items-center gap-2">
            {isAdmin && (
              <Button variant="outline" size="sm" onClick={() => setIsWardModalOpen(true)}>
                <Plus className="h-3.5 w-3.5 mr-1" />
                Add Ward
              </Button>
            )}

            {(isAdmin || isWardManager) && (
              <Button variant="primary" size="sm" onClick={() => setIsBedModalOpen(true)}>
                <Plus className="h-3.5 w-3.5 mr-1" />
                Add Bed
              </Button>
            )}

            <Button variant="outline" size="sm" onClick={fetchBeds}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh
            </Button>
          </div>
        </div>

        {/* Beds Grid */}
        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
          {filteredBeds.map((bed) => (
            <Card
              key={bed.id}
              statusBarColor={
                bed.status === 'AVAILABLE'
                  ? 'ready'
                  : bed.status === 'OCCUPIED'
                  ? 'info'
                  : bed.status === 'CLEANING_REQUIRED' || bed.status === 'UNDER_CLEANING' || (bed.status as string) === 'DIRTY'
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
                        : bed.status === 'CLEANING_REQUIRED' || bed.status === 'UNDER_CLEANING' || (bed.status as string) === 'DIRTY'
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

                <div className="space-y-2 pt-1 border-t border-clinical-border">
                  <div className="flex items-center justify-between text-[11px] text-gray-500">
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

                  {/* Quick Action Status Update */}
                  <div>
                    <label className="block text-[9px] font-bold uppercase text-gray-500 mb-0.5">Quick Status Action</label>
                    <select
                      value={bed.status}
                      onChange={(e) => handleUpdateBedStatus(bed.id, e.target.value)}
                      className="w-full rounded border border-clinical-border bg-white py-1 px-1.5 text-[11px] font-semibold text-clinical-navy focus:outline-none"
                    >
                      <option value="AVAILABLE">AVAILABLE (Ready)</option>
                      <option value="OCCUPIED">OCCUPIED</option>
                      <option value="DIRTY">DIRTY (Needs Cleaning)</option>
                      <option value="CLEANING_REQUIRED">CLEANING_REQUIRED</option>
                      <option value="UNDER_CLEANING">UNDER_CLEANING</option>
                      <option value="MAINTENANCE">MAINTENANCE</option>
                    </select>
                  </div>
                </div>
              </CardContent>
            </Card>
          ))}
        </div>

        {/* Add Bed Modal */}
        <Modal isOpen={isBedModalOpen} onClose={() => setIsBedModalOpen(false)} title="Register New Hospital Bed">
          <form onSubmit={handleCreateBed} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Bed Number / Code</label>
              <input
                type="text"
                required
                placeholder="e.g. ISO-A-05"
                value={bedForm.bedNumber}
                onChange={(e) => setBedForm({ ...bedForm, bedNumber: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Assigned Ward</label>
              <select
                required
                value={bedForm.wardId}
                onChange={(e) => setBedForm({ ...bedForm, wardId: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                {wardsList.map((w) => (
                  <option key={w.id} value={w.id}>{w.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Bed Type</label>
              <select
                value={bedForm.bedType}
                onChange={(e) => setBedForm({ ...bedForm, bedType: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                <option value="ISOLATION_NEGATIVE_PRESSURE">ISOLATION_NEGATIVE_PRESSURE</option>
                <option value="ICU">ICU</option>
                <option value="HIGH_DEPENDENCY">HIGH_DEPENDENCY</option>
                <option value="STANDARD">STANDARD</option>
              </select>
            </div>

            <div className="flex items-center gap-2">
              <input
                type="checkbox"
                id="isolationCheck"
                checked={bedForm.isIsolationCapable}
                onChange={(e) => setBedForm({ ...bedForm, isIsolationCapable: e.target.checked })}
                className="h-4 w-4 rounded border-clinical-border text-status-info focus:ring-0"
              />
              <label htmlFor="isolationCheck" className="text-xs font-semibold text-clinical-navy">
                Negative Pressure Isolation Capable (Lassa Protocol)
              </label>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsBedModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Register Bed</Button>
            </div>
          </form>
        </Modal>

        {/* Add Ward Modal */}
        <Modal isOpen={isWardModalOpen} onClose={() => setIsWardModalOpen(false)} title="Add Hospital Ward">
          <form onSubmit={handleCreateWard} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Ward Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Lassa Isolation Ward C"
                value={wardForm.name}
                onChange={(e) => setWardForm({ ...wardForm, name: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Max Bed Capacity</label>
              <input
                type="number"
                required
                min="1"
                max="50"
                value={wardForm.maxBedCapacity}
                onChange={(e) => setWardForm({ ...wardForm, maxBedCapacity: parseInt(e.target.value) || 10 })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Isolation Level</label>
              <select
                value={wardForm.isolationLevel}
                onChange={(e) => setWardForm({ ...wardForm, isolationLevel: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                <option value="HIGH_ISOLATION">HIGH_ISOLATION (Lassa Fever Unit)</option>
                <option value="MODERATE_ISOLATION">MODERATE_ISOLATION</option>
                <option value="STANDARD">STANDARD</option>
              </select>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsWardModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Create Ward</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
