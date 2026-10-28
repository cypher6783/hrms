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
  const [modalError, setModalError] = useState<string | null>(null);

  const getPatientDisplayName = (pat?: Patient | null) => {
    if (!pat) return 'Patient';
    if (pat.fullName) return pat.fullName;
    const name = `${pat.firstName || ''} ${pat.lastName || ''}`.trim();
    return name || pat.patientNumber || 'Unnamed Patient';
  };
  const navigate = useNavigate();

  // Modals for Admission & Clinical Triage & Discharge
  const [isAdmitModalOpen, setIsAdmitModalOpen] = useState(false);
  const [isAssessmentModalOpen, setIsAssessmentModalOpen] = useState(false);
  const [isDischargeModalOpen, setIsDischargeModalOpen] = useState(false);
  const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);

  // Active Admissions Map and Error States
  const [activeAdmissions, setActiveAdmissions] = useState<Record<string, { id: string; admissionNumber: string; wardId?: string; bedId?: string }>>({});
  const [admitError, setAdmitError] = useState<string | null>(null);
  const [dischargeError, setDischargeError] = useState<string | null>(null);
  const [assessmentError, setAssessmentError] = useState<string | null>(null);

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

  // Discharge Form State
  const [dischargeForm, setDischargeForm] = useState({
    dischargeOutcome: 'RECOVERED',
    dischargeNotes: 'Patient completed isolation protocol, afebrile, and stabilized.',
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
      } else if (response.data.success && Array.isArray(response.data.data)) {
        setPatients(response.data.data);
      }

      // Fetch active admissions to cross-reference patient admission status
      try {
        const admRes = await apiClient.get('/admissions');
        if (admRes.data.success) {
          const list = admRes.data.data?.content || (Array.isArray(admRes.data.data) ? admRes.data.data : []);
          const map: Record<string, { id: string; admissionNumber: string; wardId?: string; bedId?: string }> = {};
          list.forEach((adm: any) => {
            if (adm.isActive && adm.status !== 'DISCHARGED') {
              map[adm.patientId] = {
                id: adm.id,
                admissionNumber: adm.admissionNumber,
                wardId: adm.wardId,
                bedId: adm.bedId,
              };
            }
          });
          setActiveAdmissions(map);
        }
      } catch (admErr) {
        console.error('Error loading admissions map:', admErr);
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
        } else {
          setAdmitForm((prev) => ({ ...prev, bedId: '' }));
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
    setModalError(null);
    try {
      await apiClient.post('/patients', formData);
      setIsModalOpen(false);
      setFormData({
        fullName: '',
        gender: 'MALE',
        dateOfBirth: '1990-01-01',
        phoneNumber: '+234 ',
        address: 'Makurdi, Benue State',
        nextOfKinName: '',
        nextOfKinPhone: '+234 ',
      });
      fetchPatients();
    } catch (err: any) {
      console.error('Error creating patient:', err);
      setModalError(err.response?.data?.message || err.message || 'Failed to create patient. Please verify authentication.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOpenAdmitModal = (patient: Patient) => {
    setSelectedPatient(patient);
    setAdmitError(null);
    setIsAdmitModalOpen(true);
  };

  const handleOpenAssessmentModal = (patient: Patient) => {
    setSelectedPatient(patient);
    setAssessmentError(null);
    setIsAssessmentModalOpen(true);
  };

  const handleOpenDischargeModal = (patient: Patient) => {
    setSelectedPatient(patient);
    setDischargeError(null);
    setIsDischargeModalOpen(true);
  };

  const handleAdmitPatientSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatient) return;
    if (!admitForm.wardId) {
      setAdmitError('Please select a valid hospital ward');
      return;
    }
    setIsSubmitting(true);
    setAdmitError(null);
    try {
      const payload = {
        patientId: selectedPatient.id,
        wardId: admitForm.wardId,
        bedId: admitForm.bedId ? admitForm.bedId : null,
        admissionNotes: admitForm.admissionNotes,
      };
      const res = await apiClient.post('/admissions', payload);
      setIsAdmitModalOpen(false);
      fetchPatients();
      const newAdmId = res.data?.data?.id;
      if (newAdmId) {
        navigate(`/recommendations?admissionId=${newAdmId}`);
      } else {
        navigate('/recommendations');
      }
    } catch (err: any) {
      console.error('Error admitting patient:', err);
      setAdmitError(err.response?.data?.message || err.message || 'Failed to admit patient');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleAssessmentSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatient) return;
    setIsSubmitting(true);
    setAssessmentError(null);
    try {
      const activeAdm = activeAdmissions[selectedPatient.id];
      await apiClient.post('/assessments', {
        patientId: selectedPatient.id,
        admissionId: activeAdm ? activeAdm.id : undefined,
        severityLevel: assessmentForm.severityLevel,
        triageClassification: assessmentForm.triageClassification,
        infectionStatus: assessmentForm.infectionStatus,
        clinicalNotes: assessmentForm.clinicalNotes,
      });
      setIsAssessmentModalOpen(false);
      fetchPatients();
    } catch (err: any) {
      console.error('Error submitting clinical assessment:', err);
      setAssessmentError(err.response?.data?.message || err.message || 'Failed to record triage assessment');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDischargePatientSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedPatient) return;
    const activeAdm = activeAdmissions[selectedPatient.id];
    setIsSubmitting(true);
    setDischargeError(null);
    try {
      const payload = {
        dischargeOutcome: dischargeForm.dischargeOutcome,
        dischargeNotes: dischargeForm.dischargeNotes,
      };
      if (activeAdm) {
        await apiClient.put(`/admissions/${activeAdm.id}/discharge`, payload);
      } else {
        await apiClient.put(`/admissions/patient/${selectedPatient.id}/discharge`, payload);
      }
      setIsDischargeModalOpen(false);
      fetchPatients();
    } catch (err: any) {
      console.error('Error discharging patient:', err);
      setDischargeError(err.response?.data?.message || err.message || 'Failed to discharge patient');
    } finally {
      setIsSubmitting(false);
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

          <Button variant="primary" size="sm" onClick={() => { setModalError(null); setIsModalOpen(true); }}>
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
                  <TableHead>Admission Status</TableHead>
                  <TableHead className="text-right">Actions</TableHead>
                </TableRow>
              </TableHeader>
              <tbody>
                {patients.map((pat) => (
                  <TableRow key={pat.id}>
                    <TableCell className="font-semibold">
                      <div>
                        <p className="text-xs font-bold text-clinical-navy">{getPatientDisplayName(pat)}</p>
                        <p className="text-[10px] font-mono text-gray-500">{pat.patientNumber || pat.id}</p>
                      </div>
                    </TableCell>
                    <TableCell className="text-xs font-mono">
                      {pat.gender} • {pat.dateOfBirth}
                    </TableCell>
                    <TableCell>
                      <Badge variant={pat.infectionStatus === 'CONFIRMED' ? 'critical' : pat.infectionStatus === 'SUSPECTED' ? 'warning' : 'ready'}>
                        {pat.infectionStatus || 'TRIAGE_PENDING'}
                      </Badge>
                    </TableCell>
                    <TableCell>
                      {pat.isolationRequired || pat.infectionStatus === 'CONFIRMED' ? (
                        <span className="text-xs font-bold text-purple-700">REQUIRED</span>
                      ) : (
                        <span className="text-xs text-gray-400">STANDARD</span>
                      )}
                    </TableCell>
                    <TableCell>
                      {activeAdmissions[pat.id] ? (
                        <div>
                          <Badge variant="critical">ADMITTED</Badge>
                          <p className="text-[10px] font-mono text-gray-500 mt-0.5">
                            {activeAdmissions[pat.id].admissionNumber}
                          </p>
                        </div>
                      ) : (
                        <Badge variant="neutral">OUTPATIENT</Badge>
                      )}
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

                      {activeAdmissions[pat.id] ? (
                        <Button
                          variant="danger"
                          size="sm"
                          onClick={() => handleOpenDischargeModal(pat)}
                          className="text-[10px] py-0.5 px-2"
                        >
                          <LogOut className="h-3 w-3 mr-0.5" />
                          Discharge
                        </Button>
                      ) : (
                        <Button
                          variant="primary"
                          size="sm"
                          onClick={() => handleOpenAdmitModal(pat)}
                          className="text-[10px] py-0.5 px-2"
                        >
                          <UserCheck className="h-3 w-3 mr-0.5" />
                          Admit Patient
                        </Button>
                      )}
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
            {modalError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {modalError}
              </div>
            )}
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
          title={`Admit Patient: ${getPatientDisplayName(selectedPatient)}`}
        >
          <form onSubmit={handleAdmitPatientSubmit} className="space-y-4">
            {admitError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {admitError}
              </div>
            )}
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
                <option value="">-- No Bed (Allocate via AI CDS Recommendation) --</option>
                {availableBeds.map((b) => (
                  <option key={b.id} value={b.id}>{b.bedNumber}</option>
                ))}
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
          title={`Clinical Assessment: ${getPatientDisplayName(selectedPatient)}`}
        >
          <form onSubmit={handleAssessmentSubmit} className="space-y-4">
            {assessmentError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {assessmentError}
              </div>
            )}
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
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Clinical Notes & Symptoms</label>
              <textarea
                rows={3}
                value={assessmentForm.clinicalNotes}
                onChange={(e) => setAssessmentForm({ ...assessmentForm, clinicalNotes: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsAssessmentModalOpen(false)}>Cancel</Button>
              <Button type="submit" isLoading={isSubmitting}>Submit Triage</Button>
            </div>
          </form>
        </Modal>

        {/* Discharge Patient Modal */}
        <Modal
          isOpen={isDischargeModalOpen}
          onClose={() => setIsDischargeModalOpen(false)}
          title={`Discharge Patient: ${getPatientDisplayName(selectedPatient)}`}
        >
          <form onSubmit={handleDischargePatientSubmit} className="space-y-4">
            {dischargeError && (
              <div className="rounded border border-red-300 bg-red-50 p-2 text-xs font-medium text-red-700">
                {dischargeError}
              </div>
            )}
            <div className="rounded bg-amber-50 border border-amber-200 p-3 text-xs text-amber-900">
              <p className="font-semibold mb-1">Infection Control Protocol Notice:</p>
              <p>Discharging this patient will automatically release the assigned isolation bed and trigger a mandatory decontamination/cleaning task in the decontamination roster.</p>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Discharge Outcome</label>
              <select
                value={dischargeForm.dischargeOutcome}
                onChange={(e) => setDischargeForm({ ...dischargeForm, dischargeOutcome: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs font-bold"
              >
                <option value="RECOVERED">RECOVERED (Negative PCR / Asymptomatic)</option>
                <option value="TRANSFERRED">TRANSFERRED (Specialist Tertiary Facility)</option>
                <option value="DISCHARGED">DISCHARGED (Home Isolation Protocol)</option>
                <option value="DECEASED">DECEASED</option>
              </select>
            </div>

            <div>
              <label className="block text-xs font-semibold uppercase text-clinical-navy mb-1">Clinical Discharge Summary / Notes</label>
              <textarea
                rows={3}
                required
                value={dischargeForm.dischargeNotes}
                onChange={(e) => setDischargeForm({ ...dischargeForm, dischargeNotes: e.target.value })}
                className="w-full rounded border border-clinical-border p-2 text-xs"
              />
            </div>

            <div className="flex justify-end gap-2 pt-2">
              <Button type="button" variant="outline" onClick={() => setIsDischargeModalOpen(false)}>Cancel</Button>
              <Button type="submit" variant="danger" isLoading={isSubmitting}>Confirm & Release Bed</Button>
            </div>
          </form>
        </Modal>
      </div>
    </AppShell>
  );
};
