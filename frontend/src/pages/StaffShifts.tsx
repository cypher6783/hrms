import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { UserSquare2, Plus, RefreshCw } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Staff } from '../types';

export const StaffShifts: React.FC = () => {
  const [staffList, setStaffList] = useState<Staff[]>([]);
  const [wards, setWards] = useState<Array<{ id: string; name: string }>>([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [modalError, setModalError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    fullName: '',
    staffNumber: '',
    role: 'NURSE',
    specialization: 'Infectious Disease Nursing',
    wardId: '',
    contactNumber: '+234 ',
  });

  const fetchWards = async () => {
    try {
      const res = await apiClient.get('/wards');
      if (res.data.success && Array.isArray(res.data.data)) {
        setWards(res.data.data);
        if (res.data.data.length > 0 && !formData.wardId) {
          setFormData((prev) => ({ ...prev, wardId: res.data.data[0].id }));
        }
      }
    } catch (e) {
      console.error(e);
    }
  };

  const fetchStaff = async () => {
    try {
      const response = await apiClient.get('/staff');
      if (response.data.success && response.data.data) {
        const raw = response.data.data;
        const list = Array.isArray(raw?.content) ? raw.content : (Array.isArray(raw) ? raw : []);
        if (Array.isArray(list) && list.length > 0) {
          setStaffList(list);
          return;
        }
      }
      setStaffList([
        { id: 's-1', staffNumber: 'BSUTH-STF-012', fullName: 'Dr. Terver Gbande', role: 'DOCTOR', unit: 'Lassa Isolation Unit', contactNumber: '+234 803 123 4567', status: 'ACTIVE' },
        { id: 's-2', staffNumber: 'BSUTH-STF-045', fullName: 'Nurse Mercy Kever', role: 'NURSE', unit: 'Lassa Isolation Ward A', contactNumber: '+234 802 987 6543', status: 'ACTIVE' },
        { id: 's-3', staffNumber: 'BSUTH-STF-078', fullName: 'Nurse Moses Iorbee', role: 'NURSE', unit: 'Lassa Isolation Ward B (ICU)', contactNumber: '+234 805 444 3322', status: 'ACTIVE' },
        { id: 's-4', staffNumber: 'BSUTH-STF-102', fullName: 'John Tyokyaa', role: 'CLEANER', unit: 'Decontamination Unit', contactNumber: '+234 807 111 2233', status: 'ACTIVE' },
      ]);
    } catch (e) {
      console.error(e);
    }
  };

  useEffect(() => {
    fetchWards();
    fetchStaff();
  }, []);

  const handleCreateStaff = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    setModalError(null);
    try {
      const payload = {
        fullName: formData.fullName,
        role: formData.role,
        specialization: formData.specialization || 'Clinical Care',
        wardId: formData.wardId || undefined,
        availabilityStatus: 'AVAILABLE',
      };
      await apiClient.post('/staff', payload);
      setIsModalOpen(false);
      setFormData({
        fullName: '',
        staffNumber: '',
        role: 'NURSE',
        specialization: 'Infectious Disease Nursing',
        wardId: wards.length > 0 ? wards[0].id : '',
        contactNumber: '+234 ',
      });
      fetchStaff();
    } catch (err: any) {
      console.error('Error creating staff:', err);
      setModalError(err.response?.data?.message || err.message || 'Failed to register personnel');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell title="Staff & Shift Roster">
      <div className="space-y-6">
        <div className="flex items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="flex items-center gap-2">
            <UserSquare2 className="h-5 w-5 text-clinical-navy" />
            <h3 className="text-xs font-bold uppercase tracking-wider text-clinical-navy">
              Lassa Isolation Unit Personnel & Shift Management
            </h3>
          </div>
          <div className="flex items-center gap-2">
            <Button variant="primary" size="sm" onClick={() => { setModalError(null); setIsModalOpen(true); }}>
              <Plus className="h-4 w-4 mr-1" />
              Register Staff Member
            </Button>
            <Button variant="outline" size="sm" onClick={fetchStaff}>
              <RefreshCw className="h-3.5 w-3.5 mr-1" />
              Refresh Roster
            </Button>
          </div>
        </div>

        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <UserSquare2 className="h-4 w-4 text-clinical-navy" />
              <span>Clinical Personnel Roster</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Staff Member</TableHead>
                  <TableHead>Role</TableHead>
                  <TableHead>Assigned Unit</TableHead>
                  <TableHead>Contact</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {staffList.map((stf) => (
                  <TableRow key={stf.id}>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{stf.fullName}</p>
                        <p className="text-[10px] font-mono text-gray-500">{stf.staffNumber}</p>
                      </div>
                    </TableCell>
                    <TableCell><Badge variant="neutral">{stf.role}</Badge></TableCell>
                    <TableCell className="text-xs">{stf.specialization || stf.unit || "Isolation Unit"}</TableCell>
                    <TableCell className="font-mono text-xs">{stf.contactNumber || "N/A"}</TableCell>
                    <TableCell><Badge variant="ready">{stf.availabilityStatus || stf.status || "AVAILABLE"}</Badge></TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>

        {/* Register Staff Modal */}
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Clinical Staff Member">
          <form onSubmit={handleCreateStaff} className="space-y-4">
            {modalError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {modalError}
              </div>
            )}
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Full Name & Title</label>
              <input
                type="text"
                required
                placeholder="e.g. Nurse Amina Lawal"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Role</label>
                <select
                  value={formData.role}
                  onChange={(e) => setFormData({ ...formData, role: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                >
                  <option value="NURSE">NURSE</option>
                  <option value="DOCTOR">DOCTOR</option>
                  <option value="MEDICAL_DOCTOR">MEDICAL DOCTOR</option>
                  <option value="HYGIENE_STAFF">HYGIENE / CLEANER</option>
                  <option value="WARD_MANAGER">WARD MANAGER</option>
                  <option value="ADMINISTRATOR">ADMINISTRATOR</option>
                </select>
              </div>

              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Specialization / Department</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Lassa Isolation Care"
                  value={formData.specialization}
                  onChange={(e) => setFormData({ ...formData, specialization: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                />
              </div>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Assigned Ward</label>
              <select
                value={formData.wardId}
                onChange={(e) => setFormData({ ...formData, wardId: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                <option value="">-- No Specific Ward --</option>
                {wards.map((w) => (
                  <option key={w.id} value={w.id}>{w.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Contact Phone Number</label>
              <input
                type="text"
                required
                placeholder="+234 803 000 0000"
                value={formData.contactNumber}
                onChange={(e) => setFormData({ ...formData, contactNumber: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Register Personnel</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
