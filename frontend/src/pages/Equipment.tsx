import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Stethoscope, Plus, RefreshCw, Wrench, CheckCircle2 } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Equipment as EquipmentModel } from '../types';

import { useAuth } from '../auth/AuthContext';

export const EquipmentPage: React.FC = () => {
  const { user } = useAuth();
  const userRole = (user?.role || '').toUpperCase();
  const isAdmin = userRole === 'ADMINISTRATOR' || userRole === 'ADMIN';
  const isWardManager = userRole === 'WARD_MANAGER';
  const isEquipmentOfficer = userRole === 'EQUIPMENT_OFFICER';
  const canRegister = isAdmin || isWardManager || isEquipmentOfficer;

  const [equipmentList, setEquipmentList] = useState<EquipmentModel[]>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);
  const [wards, setWards] = useState<Array<{ id: string; name: string }>>([]);

  const [formData, setFormData] = useState({
    serialNumber: '',
    name: '',
    category: 'VENTILATOR',
    locationWardId: '',
    status: 'AVAILABLE',
  });

  const fetchWards = async () => {
    try {
      const res = await apiClient.get('/wards');
      if (res.data.success && Array.isArray(res.data.data)) {
        setWards(res.data.data);
        if (res.data.data.length > 0) {
          setFormData((prev) => ({ ...prev, locationWardId: res.data.data[0].id }));
        }
      }
    } catch (e) {
      console.error(e);
    }
  };

  const fetchEquipment = async () => {
    try {
      const response = await apiClient.get('/equipment');
      if (response.data.success && Array.isArray(response.data.data)) {
        setEquipmentList(response.data.data);
      } else {
        setEquipmentList([
          { id: 'eq-1', serialNumber: 'VENT-BSUTH-01', name: 'Hamilton C3 Mechanical Ventilator', category: 'VENTILATOR', status: 'IN_USE', locationWardName: 'Lassa Isolation Ward B (ICU)' },
          { id: 'eq-2', serialNumber: 'VENT-BSUTH-02', name: 'Mindray SV300 Ventilator', category: 'VENTILATOR', status: 'AVAILABLE', locationWardName: 'Lassa Isolation Ward A' },
          { id: 'eq-3', serialNumber: 'PUMP-BSUTH-09', name: 'B.Braun Infusomat Space Pump', category: 'INFUSION_PUMP', status: 'UNDER_MAINTENANCE', locationWardName: 'Biomedical Workshop' },
          { id: 'eq-4', serialNumber: 'MON-BSUTH-04', name: 'Philips Intellivue Patient Monitor', category: 'PATIENT_MONITOR', status: 'AVAILABLE', locationWardName: 'High Dependency Unit (HDU)' },
        ]);
      }
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchWards();
    fetchEquipment();
  }, []);

  const handleCreateEquipment = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setModalError(null);
    try {
      const selectedWard = wards.find((w) => w.id === formData.locationWardId);
      const payload = {
        name: formData.name,
        equipmentType: formData.category,
        serialNumber: formData.serialNumber,
        location: selectedWard ? selectedWard.name : 'BSUTH Isolation Facility',
        assignedWardId: formData.locationWardId || undefined,
      };
      await apiClient.post('/equipment', payload);
      setIsModalOpen(false);
      setFormData({
        serialNumber: '',
        name: '',
        category: 'VENTILATOR',
        locationWardId: wards.length > 0 ? wards[0].id : '',
        status: 'AVAILABLE',
      });
      fetchEquipment();
    } catch (err: any) {
      console.error('Error creating equipment:', err);
      setModalError(err.response?.data?.message || err.message || 'Failed to register equipment');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleUpdateStatus = async (id: string, status: string) => {
    try {
      await apiClient.put(`/equipment/${id}/status?status=${status}`);
      fetchEquipment();
    } catch (err) {
      console.error('Error updating equipment status:', err);
      setEquipmentList((prev) => prev.map((eq) => (eq.id === id ? { ...eq, status: status as any } : eq)));
    }
  };

  return (
    <AppShell title="Medical Equipment Catalogue">
      <div className="space-y-6">
        <div className="flex items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex items-center gap-2">
            <Stethoscope className="h-5 w-5 text-clinical-navy" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
              Critical Lassa Isolation Unit Equipment Tracker
            </h3>
          </div>
          <div className="flex items-center gap-2">
            {canRegister && (
              <Button variant="primary" size="sm" onClick={() => { setModalError(null); setIsModalOpen(true); }}>
                <Plus className="h-4 w-4 mr-1" />
                Register New Equipment
              </Button>
            )}
            <Button variant="outline" size="sm" onClick={fetchEquipment}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh
            </Button>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Stethoscope className="h-4 w-4 text-clinical-navy" />
              <span>Medical Equipment Inventory</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Serial Number & Equipment</TableHead>
                  <TableHead>Category</TableHead>
                  <TableHead>Location Ward</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Action</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {equipmentList.map((eq) => (
                  <TableRow key={eq.id}>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{eq.name}</p>
                        <p className="text-[10px] font-mono text-gray-500">{eq.serialNumber}</p>
                      </div>
                    </TableCell>
                    <TableCell><Badge variant="neutral">{eq.category || eq.equipmentType || "EQUIPMENT"}</Badge></TableCell>
                    <TableCell className="text-xs">{eq.locationWardName || 'Central Supply'}</TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          eq.status === 'AVAILABLE'
                            ? 'ready'
                            : eq.status === 'IN_USE'
                            ? 'info'
                            : 'critical'
                        }
                      >
                        {eq.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right space-x-1">
                      {eq.status !== 'AVAILABLE' && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleUpdateStatus(eq.id, 'AVAILABLE')}
                          className="text-[10px] text-emerald-700 py-0.5 px-2"
                        >
                          <CheckCircle2 className="h-3 w-3 mr-0.5" />
                          Set Available
                        </Button>
                      )}
                      {eq.status !== 'UNDER_MAINTENANCE' && (
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => handleUpdateStatus(eq.id, 'UNDER_MAINTENANCE')}
                          className="text-[10px] text-amber-700 py-0.5 px-2"
                        >
                          <Wrench className="h-3 w-3 mr-0.5" />
                          Send Maintenance
                        </Button>
                      )}
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>

        {/* Register Equipment Modal */}
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Medical Equipment">
          <form onSubmit={handleCreateEquipment} className="space-y-4">
            {modalError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {modalError}
              </div>
            )}
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Equipment Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Hamilton C3 Mechanical Ventilator"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Serial Number / Tag</label>
              <input
                type="text"
                required
                placeholder="e.g. VENT-BSUTH-05"
                value={formData.serialNumber}
                onChange={(e) => setFormData({ ...formData, serialNumber: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Category</label>
                <select
                  value={formData.category}
                  onChange={(e) => setFormData({ ...formData, category: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                >
                  <option value="VENTILATOR">VENTILATOR</option>
                  <option value="INFUSION_PUMP">INFUSION_PUMP</option>
                  <option value="PATIENT_MONITOR">PATIENT_MONITOR</option>
                  <option value="DIALYSIS_MACHINE">DIALYSIS_MACHINE</option>
                  <option value="DEFIBRILLATOR">DEFIBRILLATOR</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Location Ward</label>
                <select
                  value={formData.locationWardId}
                  onChange={(e) => setFormData({ ...formData, locationWardId: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                >
                  {wards.map((w) => (
                    <option key={w.id} value={w.id}>{w.name}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Register Equipment</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
