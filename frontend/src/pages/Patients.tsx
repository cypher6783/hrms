import React, { useEffect, useState } from 'react';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Table, TableHeader, TableRow, TableHead, TableCell } from '../components/ui/Table';
import { Modal } from '../components/ui/Modal';
import { Search, UserPlus, ShieldAlert, Activity, UserCheck, LogOut } from 'lucide-react';
import { apiClient } from '../api/client';
import type { Patient, PatientRequest } from '../types';
import { useNavigate } from 'react-router-dom';

export const Patients: React.FC = () => {
  const [patients, setPatients] = useState<Patient[]>([]);
  const [search, setSearch] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const navigate = useNavigate();

  // Modals for Admission & Clinical Triage
  const [isAdmitModalOpen, setIsAdmitModalOpen] = useState(false);
  const [isAssessmentModalOpen, setIsAssessmentModalOpen] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);

  // Wards and Available Beds for Admission Form
  const [availableWards, setAvailableWards] = useState<Array<{ id: string; name: string }>>([]);
  const [availableBeds, setAvailableBeds] = useState<Array<{ id: string; bedNumber: string }>>([]);

  // Registration Form State
  const [formData, setFormData] = useState<PatientRequest>({
    fullName: '',
    gender: 'MALE',
    dateOfBirth: '1990-01-01',
    phoneNumber: '+234 ',
    address: 'Makurdi, Benue State',
    nextOfKinName: '',
    nextOfKinPhone: '+234 ',
  });

  // Admission Form State
  const [admitForm, setAdmitForm] = useState({
    wardId: '',
    bedId: '',
    admissionNotes: 'Acute Lassa fever triage admission',
  });

  // Clinical Assessment Triage Form State
  const [assessmentForm, setAssessmentForm] = useState({
    severityLevel: 'CRITICAL',
    triageClassification: 'HIGH_RISK',
    infectionStatus: 'CONFIRMED_LASSA',
    clinicalNotes: 'Patient presents with severe viral hemorrhagic symptoms.',
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
        ]);
      }
    } catch (e) {
      console.error('Error loading patients:', e);
    }
  };

  const fetchWardsAndBeds = async () => {
    try {
      const wardRes = await apiClient.get('/wards');
      if (wardRes.data.success && Array.isArray(wardRes.data.data)) {
        setAvailableWards(wardRes.data.data);
        if (wardRes.data.data.length > 0) {
          const firstWardId = wardRes.data.data[0].id;
          setAdmitForm((prev) => ({ ...prev, wardId: firstWardId }));
          fetchAvailableBedsForWard(firstWardId);
        }
      }
    } catch (e) {
      console.error('Error loading wards:', e);
    }
  };

  const fetchAvailableBedsForWard = async (wardId: string) => {
    try {
      const bedRes = await apiClient.get('/beds/filter', { params: { wardId, status: 'AVAILABLE' } });
      if (bedRes.data.success && Array.isArray(bedRes.data.data)) {
        setAvailableBeds(bedRes.data.data);
        if (bedRes.data.data.length > 0) {
          setAdmitForm((prev) => ({ ...prev, bedId: bedRes.data.data[0].id }));
        }
      }
    } catch (e) {
      console.error('Error loading available beds:', e);
    }
  };

  useEffect(() => {
    fetchPatients();
    fetchWardsAndBeds();
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

  const handleOpenAdmitModal = (patient: Patient) => {
    setSelectedPatient(patient);
    setIsAdmitModalOpen(true);
  };

  const handleOpenAssessmentModal = (patient: Patient) => {
    setSelectedPatient(patient);
    setIsAssessmentModalOpen(true);
  };

  const handleAdmitPatientSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatient) return;
    setIsSubmitting(true);
    try {
      await apiClient.post('/admissions', {
        patientId: selectedPatient.id,
        wardId: admitForm.wardId,
        bedId: admitForm.bedId,
        admissionNotes: admitForm.admissionNotes,
      });
      setIsAdmitModalOpen(false);
      fetchPatients();
      navigate('/recommendations');
    } catch (err) {
      console.error('Error admitting patient:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAssessmentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatient) return;
    setIsSubmitting(true);
    try {
      await apiClient.post('/assessments', {
        patientId: selectedPatient.id,
        severityLevel: assessmentForm.severityLevel,
        triageClassification: assessmentForm.triageClassification,
        infectionStatus: assessmentForm.infectionStatus,
        clinicalNotes: assessmentForm.clinicalNotes,
      });
      setIsAssessmentModalOpen(false);
      fetchPatients();
    } catch (err) {
      console.error('Error submitting clinical assessment:', err);
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDischargePatient = async (patient: Patient) => {
    if (!window.confirm(`Discharge patient ${patient.firstName} ${patient.lastName}? This will release the bed for decontamination.`)) return;
    try {
      await apiClient.post(`/admissions/patient/${patient.id}/discharge`);
      fetchPatients();
    } catch (err) {
      console.error('Error discharging patient:', err);
    }
  };

  return (
    <AppShell title="Patient Management & Admissions">
      <div className="space-y-6">
        {/* Top Control Bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-3 shadow-xs">
          <div className="relative flex-1 min-w-[240px]">
            <Search className="absolute left-3 top-2.5 h-4 w-4 text-gray-400" />
            <input
              type="text"
              placeholder="Search by Patient ID, Name, or Hospital Number..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              className="w-full rounded border border-clinical-border bg-white py-1.5 pl-9 pr-3 text-xs focus:border-status-info focus:outline-none"
            />
          </div>

          <Button variant="primary" size="sm" onClick={() => setIsModalOpen(true)}>
            <UserPlus className="h-4 w-4 mr-1" />
            Register New Patient
          </Button>
        </div>

        {/* Patients Table */}
        <Card>
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <ShieldAlert className="h-4 w-4 text-red-600" />
              <span>Registered Patients & Clinical Triage Status</span>
            </CardTitle>
          </CardHeader>
          <CardContent className="p-0">
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>Patient Details</TableHead>
                  <TableHead>Gender / DOB</TableHead>
                  <TableHead>Infection Status</TableHead>
                  <TableHead>Isolation Required</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {patients.map((pat) => (
                  <TableRow key={pat.id}>
                    <TableCell className="font-semibold">
                      <div>
                        <p className="text-xs font-bold text-clinical-navy">{pat.firstName} {pat.lastName}</p>
                        <p className="text-[10px] font-mono text-gray-500">{pat.patientNumber || pat.id}</p>
                      </div>
                    </TableCell>
                    <TableCell className="text-xs font-mono">
                      {pat.gender} • {pat.dateOfBirth}
                    </TableCell>
                    <TableCell>
                      <Badge variant={pat.infectionStatus === 'CONFIRMED' ? 'critical' : pat.infectionStatus === 'SUSPECTED' ? 'warning' : 'ready'}>
                        {pat.infectionStatus}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      {pat.isolationRequired ? (
                        <span className="text-xs font-bold text-purple-700">REQUIRED</span>
                      ) : (
                        <span className="text-xs text-gray-400">NO</span>
                      )}
                    </TableCell>
                    <TableCell>
                      <Badge variant={pat.status === 'ACTIVE' ? 'ready' : 'neutral'}>
                        {pat.status}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-right space-x-1">
                      <Button
                        variant="outline"
                        size="sm"
                        onClick={() => handleOpenAssessmentModal(pat)}
                        className="text-[10px] py-0.5 px-2"
                      >
                        <Activity className="h-3 w-3 mr-0.5" />
                        Triage Assessment
                      </Button>

                      <Button
                        variant="primary"
                        size="sm"
                        onClick={() => handleOpenAdmitModal(pat)}
                        className="text-[10px] py-0.5 px-2"
                      >
                        <UserCheck className="h-3 w-3 mr-0.5" />
                        Admit Patient
                      </Button>

                      <Button
                        variant="danger"
                        size="sm"
                        onClick={() => handleDischargePatient(pat)}
                        className="text-[10px] py-0.5 px-2"
                      >
                        <LogOut className="h-3 w-3 mr-0.5" />
                        Discharge
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </tbody>
            </Table>
          </CardContent>
        </Card>

        {/* Register Patient Modal */}
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Patient for Lassa Triage">
          <form onSubmit={handleCreatePatient} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Full Patient Name</label>
              <input
                type="text"
                required
                placeholder="e.g. Terver Akum"
                value={formData.fullName}
                onChange={(e) => setFormData({ ...formData, fullName: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-semibold"
              />
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Gender</label>
                <select
                  value={formData.gender}
                  onChange={(e) => setFormData({ ...formData, gender: e.target.value as any })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                >
                  <option value="MALE">MALE</option>
                  <option value="FEMALE">FEMALE</option>
                </select>
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Date of Birth</label>
                <input
                  type="date"
                  required
                  value={formData.dateOfBirth}
                  onChange={(e) => setFormData({ ...formData, dateOfBirth: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Phone Number</label>
                <input
                  type="text"
                  required
                  value={formData.phoneNumber}
                  onChange={(e) => setFormData({ ...formData, phoneNumber: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Address / Locality</label>
                <input
                  type="text"
                  required
                  value={formData.address}
                  onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                />
              </div>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Next of Kin Name</label>
                <input
                  type="text"
                  required
                  placeholder="e.g. Aondona Akum"
                  value={formData.nextOfKinName}
                  onChange={(e) => setFormData({ ...formData, nextOfKinName: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs"
                />
              </div>
              <div>
                <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Next of Kin Phone</label>
                <input
                  type="text"
                  required
                  value={formData.nextOfKinPhone}
                  onChange={(e) => setFormData({ ...formData, nextOfKinPhone: e.target.value })}
                  className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
                />
              </div>
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Register Patient</Button>
            </div>
          </form>
        </Modal>

        {/* Admit Patient Modal */}
        <Modal
          isOpen={isAdmitModalOpen}
          onClose={() => setIsAdmitModalOpen(false)}
          title={`Admit Patient: ${selectedPatient?.firstName || ''} ${selectedPatient?.lastName || ''}`}
        >
          <form onSubmit={handleAdmitPatientSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Select Ward</label>
              <select
                value={admitForm.wardId}
                onChange={(e) => {
                  setAdmitForm({ ...admitForm, wardId: e.target.value });
                  fetchAvailableBedsForWard(e.target.value);
                }}
                className="w-full rounded border border-clinical-border p-2 text-xs font-semibold"
              >
                {availableWards.map((w) => (
                  <option key={w.id} value={w.id}>{w.name}</option>
                ))}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Select Available Bed</label>
              <select
                value={admitForm.bedId}
                onChange={(e) => setAdmitForm({ ...admitForm, bedId: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-mono"
              >
                {availableBeds.length === 0 ? (
                  <option value="">No Available Beds in Ward</option>
                ) : (
                  availableBeds.map((b) => (
                    <option key={b.id} value={b.id}>{b.bedNumber}</option>
                  ))
                )}
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Admission Clinical Notes</label>
              <textarea
                rows={3}
                value={admitForm.admissionNotes}
                onChange={(e) => setAdmitForm({ ...admitForm, admissionNotes: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsAdmitModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Confirm Admission</Button>
            </div>
          </form>
        </Modal>

        {/* Clinical Assessment Modal */}
        <Modal
          isOpen={isAssessmentModalOpen}
          onClose={() => setIsAssessmentModalOpen(false)}
          title={`Clinical Assessment: ${selectedPatient?.firstName || ''} ${selectedPatient?.lastName || ''}`}
        >
          <form onSubmit={handleAssessmentSubmit} className="space-y-4">
            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Severity Level</label>
              <select
                value={assessmentForm.severityLevel}
                onChange={(e) => setAssessmentForm({ ...assessmentForm, severityLevel: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-bold"
              >
                <option value="CRITICAL">CRITICAL (Hemorrhagic / Shock Risk)</option>
                <option value="HIGH">HIGH (High Fever / Organ Involvement)</option>
                <option value="MODERATE">MODERATE</option>
                <option value="MILD">MILD</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Triage Classification</label>
              <select
                value={assessmentForm.triageClassification}
                onChange={(e) => setAssessmentForm({ ...assessmentForm, triageClassification: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                <option value="HIGH_RISK">HIGH_RISK_LASSA_ISOLATION</option>
                <option value="SUSPECTED_MONITORING">SUSPECTED_MONITORING</option>
                <option value="LOW_RISK">LOW_RISK</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Infection Confirmation</label>
              <select
                value={assessmentForm.infectionStatus}
                onChange={(e) => setAssessmentForm({ ...assessmentForm, infectionStatus: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              >
                <option value="CONFIRMED_LASSA">CONFIRMED_LASSA (Lab Positive)</option>
                <option value="SUSPECTED_LASSA">SUSPECTED_LASSA (Pending PCR)</option>
                <option value="NEGATIVE">NEGATIVE</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Clinical Notes</label>
              <textarea
                rows={3}
                value={assessmentForm.clinicalNotes}
                onChange={(e) => setAssessmentForm({ ...assessmentForm, clinicalNotes: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsAssessmentModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Submit Assessment</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
