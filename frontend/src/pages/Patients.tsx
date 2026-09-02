import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Modal } from '../components/ui/Modal';
import { Search, UserPlus, ShieldAlert } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Patient, PatientRequest } from '../types';
import { useNavigate } from 'react-router-dom';

export const Patients: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  const [formData, setFormData] = useState<PatientRequest>({
    firstName: '',
    lastName: '',
    gender: 'MALE',
    dateOfBirth: '1990-01-01',
    contactNumber: '',
    emergencyContact: '',
    infectionStatus: 'CONFIRMED',
    isolationRequired: true,
    careLevel: 'ISOLATION',
  });

  const fetchPatients = async () => {
    try {
      const response = await apiClient.get('/patients', { params: { search, size: 20 } });
      if (response.data.success && response.data.data?.content) {
        setPatients(response.data.data.content);
      } else {
        setPatients([
          {
            id: 'pat-001',
            patientNumber: 'PAT-2026-001',
            firstName: 'Terna',
            lastName: 'Akura',
            gender: 'MALE',
            dateOfBirth: '1984-06-12',
            infectionStatus: 'CONFIRMED',
            isolationRequired: true,
            careLevel: 'ISOLATION',
            status: 'ACTIVE',
            createdAt: '2026-09-01T08:30:00Z',
            updatedAt: '2026-09-01T08:30:00Z',
          },
          {
            id: 'pat-002',
            patientNumber: 'PAT-2026-002',
            firstName: 'Dooshima',
            lastName: 'Orban',
            gender: 'FEMALE',
            dateOfBirth: '1992-11-23',
            infectionStatus: 'SUSPECTED',
            isolationRequired: true,
            careLevel: 'HIGH_DEPENDENCY',
            status: 'ACTIVE',
            createdAt: '2026-09-01T10:15:00Z',
            updatedAt: '2026-09-01T10:15:00Z',
          },
          {
            id: 'pat-003',
            patientNumber: 'PAT-2026-003',
            firstName: 'Aondona',
            lastName: 'Tsevende',
            gender: 'MALE',
            dateOfBirth: '1978-03-04',
            infectionStatus: 'CLEARED',
            isolationRequired: false,
            careLevel: 'GENERAL',
            status: 'ACTIVE',
            createdAt: '2026-08-28T14:00:00Z',
            updatedAt: '2026-08-30T11:00:00Z',
          },
        ]);
      }
    } catch (e) {
      console.error('Error loading patients:', e);
    }
  };

  useEffect(() => {
    fetchPatients();
  }, [search]);

  const handleCreatePatient = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      await apiClient.post('/patients', formData);
      setIsModalOpen(false);
      fetchPatients();
    } catch (e) {
      console.error('Error creating patient:', e);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell title="Patient Management">
      <div className="space-y-6">
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
          <div className="relative flex-1 max-w-md">
            <Search className="pointer-events-none absolute inset-y-0 left-0 my-auto ml-3 h-4 w-4 text-gray-400" />
            <input
              type="text"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              placeholder="Search by Patient Name or Number (e.g. PAT-2026-001)..."
              className="w-full rounded border border-clinical-border bg-white py-1.5 pl-9 pr-3 text-xs focus:border-status-info focus:outline-none"
            />
          </div>

          <Button variant="primary" size="sm" onClick={() => setIsModalOpen(true)}>
            <UserPlus className="h-4 w-4 mr-1.5" />
            Register Patient
          </Button>
        </div>

        <Card>
          <CardHeader>
            <CardTitle>Clinical Patient Directory</CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Patient ID & Name</TableHead>
                  <TableHead>Gender / DOB</TableHead>
                  <TableHead>Infection Status</TableHead>
                  <TableHead>Care Level</TableHead>
                  <TableHead>Isolation</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Action</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {patients.map((pat) => (
                  <TableRow key={pat.id}>
                    <TableCell>
                      <div>
                        <p className="font-bold text-clinical-navy">{pat.firstName} {pat.lastName}</p>
                        <p className="text-[10px] font-mono text-gray-500">{pat.patientNumber}</p>
                      </div>
                    </TableCell>
                    <TableCell className="font-mono">
                      {pat.gender} | {pat.dateOfBirth}
                    </TableCell>
                    <TableCell>
                      <Badge
                        variant={
                          pat.infectionStatus === 'CONFIRMED'
                            ? 'critical'
                            : pat.infectionStatus === 'SUSPECTED'
                            ? 'warning'
                            : 'ready'
                        }
                      >
                        {pat.infectionStatus}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Badge variant={pat.careLevel === 'ICU' ? 'icu' : 'neutral'}>
                        {pat.careLevel}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      {pat.isolationRequired ? (
                        <span className="inline-flex items-center gap-1 text-[11px] font-bold text-purple-700">
                          <ShieldAlert className="h-3.5 w-3.5 text-purple-600" />
                          REQUIRED
                        </span>
                      ) : (
                        <span className="text-[11px] text-gray-400">Standard</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <Badge variant={pat.status === 'ACTIVE' ? 'info' : 'neutral'}>
                        {pat.status}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => navigate(`/recommendations?patientId=${pat.id}`)}
                        className="text-[11px]"
                      >
                        Allocate Resource
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>
      </div>

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Patient">
        <form onSubmit={handleCreatePatient} className="space-y-3">
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">First Name *</label>
              <input
                type="text"
                required
                value={formData.firstName}
                onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs"
              />
            </div>
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Last Name *</label>
              <input
                type="text"
                required
                value={formData.lastName}
                onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Gender *</label>
              <select
                value={formData.gender}
                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs"
              >
                <option value="MALE">Male</option>
                <option value="FEMALE">Female</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Date of Birth *</label>
              <input
                type="date"
                required
                value={formData.dateOfBirth}
                onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs font-mono"
              />
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Infection Status *</label>
              <select
                value={formData.infectionStatus}
                onChange={(e) => setFormData({ ...formData, infectionStatus: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs font-bold"
              >
                <option value="CONFIRMED">CONFIRMED (Lassa)</option>
                <option value="SUSPECTED">SUSPECTED</option>
                <option value="NEGATIVE">NEGATIVE</option>
              </select>
            </div>
            <div>
              <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Care Level *</label>
              <select
                value={formData.careLevel}
                onChange={(e) => setFormData({ ...formData, careLevel: e.target.value })}
                className="w-full rounded border border-clinical-border bg-white p-2 text-xs"
              >
                <option value="ISOLATION">Isolation Ward</option>
                <option value="ICU">ICU Intensive</option>
                <option value="HIGH_DEPENDENCY">High Dependency (HDU)</option>
                <option value="GENERAL">General</option>
              </select>
            </div>
          </div>

          <div className="flex items-center gap-2 pt-2">
            <input
              type="checkbox"
              id="isolationRequired"
              checked={formData.isolationRequired}
              onChange={(e) => setFormData({ ...formData, isolationRequired: e.target.checked })}
              className="h-4 w-4 rounded border-gray-300 text-status-critical"
            />
            <label htmlFor="isolationRequired" className="text-xs font-bold text-status-critical uppercase">
              Mandatory Isolation Required
            </label>
          </div>

          <div className="flex justify-end gap-2 pt-4">
            <Button variant="outline" size="sm" type="button" onClick={() => setIsModalOpen(false)}>Cancel</Button>
            <Button variant="primary" size="sm" type="submit" isLoading={isSubmitting}>Register Patient</Button>
          </div>
        </form>
      </Modal>
    </AppShell>
  );
};
