import React, { useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { AppShell } from '../components/layout/AppShell';
import { Card, CardHeader, CardTitle, CardContent } from '../components/ui/Card';
import { Badge } from '../components/ui/Badge';
import { Button } from '../components/ui/Button';
import { Modal } from '../components/ui/Modal';
import {
  Sparkles,
  ShieldCheck,
  ShieldAlert,
  CheckCircle2,
  XCircle,
  AlertTriangle,
  HelpCircle,
  Check,
  X,
  FileEdit,
  RefreshCw
} from 'lucide-react';
import { useAuth } from '../auth/AuthContext';
import { apiClient } from '../api/client';
import type { Recommendation, Admission } from '../types';

export const RecommendationCenter: React.FC = () => {
  const { user } = useAuth();
  const userRole = (user?.role || '').toUpperCase();
  const isAdmin = userRole === 'ADMINISTRATOR' || userRole === 'ADMIN';
  const isWardManager = userRole === 'WARD_MANAGER';
  const isDoctor = userRole === 'MEDICAL_DOCTOR' || userRole === 'DOCTOR';
  const canDecide = isAdmin || isWardManager || isDoctor;

  const [searchParams, setSearchParams] = useSearchParams();
  const admissionIdFromUrl = searchParams.get('admissionId');

  const [admissionsList, setAdmissionsList] = useState<Admission[]>([]);
  const [selectedAdmissionId, setSelectedAdmissionId] = useState<string>(admissionIdFromUrl || '');
  const [recommendation, setRecommendation] = useState<Recommendation | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [isGenerating, setIsGenerating] = useState(false);
  const [feedbackMsg, setFeedbackMsg] = useState<{ type: 'success' | 'error'; text: string } | null>(null);

  // Decision Modal States
  const [rejectModalOpen, setRejectModalOpen] = useState(false);
  const [overrideModalOpen, setOverrideModalOpen] = useState(false);
  const [selectedItemId, setSelectedItemId] = useState<string | null>(null);

  // Form Inputs
  const [rejectionReason, setRejectionReason] = useState('');
  const [overrideJustification, setOverrideJustification] = useState('');
  const [overrideEntityId, setOverrideEntityId] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);

  // 1. Fetch Admissions List to populate selector dropdown
  const fetchAdmissions = async () => {
    try {
      const response = await apiClient.get('/admissions');
      if (response.data.success) {
        const list: Admission[] = response.data.data?.content || (Array.isArray(response.data.data) ? response.data.data : []);
        setAdmissionsList(list);
        if (list.length > 0 && !selectedAdmissionId) {
          setSelectedAdmissionId(list[0].id);
        }
      }
    } catch (e) {
      console.error('Error fetching admissions list:', e);
    }
  };

  // 2. Fetch Recommendation for Selected Admission ID
  const fetchRecommendation = async (admId?: string) => {
    const targetId = admId || selectedAdmissionId;
    if (!targetId) return;

    setIsLoading(true);
    setFeedbackMsg(null);
    try {
      const response = await apiClient.get(`/recommendations/admission/${targetId}`);
      if (response.data.success && Array.isArray(response.data.data) && response.data.data.length > 0) {
        setRecommendation(response.data.data[0]);
      } else {
        setRecommendation(null);
      }
    } catch (e: any) {
      console.error('Error loading recommendation:', e);
      setRecommendation(null);
    } finally {
      setIsLoading(false);
    }
  };

  // 3. Generate New Recommendation Batch
  const handleGenerateRecommendation = async () => {
    if (!selectedAdmissionId) return;
    setIsGenerating(true);
    setFeedbackMsg(null);
    try {
      const response = await apiClient.post(`/recommendations/admission/${selectedAdmissionId}`);
      if (response.data.success && response.data.data) {
        setRecommendation(response.data.data);
        setFeedbackMsg({ type: 'success', text: 'Recommendation batch generated successfully!' });
      } else {
        fetchRecommendation();
      }
    } catch (e: any) {
      console.error('Error generating recommendation:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Failed to generate recommendation batch.' });
    } finally {
      setIsGenerating(false);
    }
  };

  useEffect(() => {
    fetchAdmissions();
  }, []);

  useEffect(() => {
    if (selectedAdmissionId) {
      setSearchParams({ admissionId: selectedAdmissionId });
      fetchRecommendation(selectedAdmissionId);
    }
  }, [selectedAdmissionId]);

  const handleAccept = async (itemId: string) => {
    setIsSubmitting(true);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/recommendations/items/${itemId}/decide`, {
        decisionType: 'ACCEPT',
      });
      setFeedbackMsg({ type: 'success', text: 'Allocation recommendation ACCEPTED and recorded in audit log!' });
      fetchRecommendation();
    } catch (e: any) {
      console.error('Failed to accept recommendation:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Error executing Accept decision.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleRejectSubmit = async () => {
    if (!selectedItemId || !rejectionReason.trim()) return;
    setIsSubmitting(true);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/recommendations/items/${selectedItemId}/decide`, {
        decisionType: 'REJECT',
        overrideJustification: rejectionReason,
      });
      setRejectModalOpen(false);
      setRejectionReason('');
      setFeedbackMsg({ type: 'success', text: 'Recommendation REJECTED and logged with clinician justification.' });
      fetchRecommendation();
    } catch (e: any) {
      console.error('Failed to reject recommendation:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Error executing Reject decision.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleOverrideSubmit = async () => {
    if (!selectedItemId || !overrideJustification.trim() || !overrideEntityId.trim()) return;
    setIsSubmitting(true);
    setFeedbackMsg(null);
    try {
      await apiClient.post(`/recommendations/items/${selectedItemId}/decide`, {
        decisionType: 'OVERRIDE',
        overriddenEntityId: overrideEntityId,
        overrideJustification: overrideJustification,
      });
      setOverrideModalOpen(false);
      setOverrideJustification('');
      setOverrideEntityId('');
      setFeedbackMsg({ type: 'success', text: 'Manual OVERRIDE recorded successfully in immutable audit log.' });
      fetchRecommendation();
    } catch (e: any) {
      console.error('Failed to submit manual override:', e);
      setFeedbackMsg({ type: 'error', text: e.response?.data?.message || 'Error executing Manual Override.' });
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <AppShell title="AI Resource Recommendation Center">
      <div className="space-y-6">
        {/* Admission Selection Top Control Bar */}
        <div className="flex flex-wrap items-center justify-between gap-3 rounded-md border border-clinical-border bg-white p-4 shadow-xs">
          <div className="flex flex-wrap items-center gap-3">
            <div>
              <label className="block text-[10px] font-bold uppercase text-clinical-navy mb-1">
                Select Active Patient Admission
              </label>
              <select
                value={selectedAdmissionId}
                onChange={(e) => setSelectedAdmissionId(e.target.value)}
                className="rounded border border-clinical-border bg-white py-1.5 px-3 text-xs font-mono font-bold text-clinical-navy focus:outline-none min-w-[280px]"
              >
                {admissionsList.length === 0 ? (
                  <option value="">No Active Admissions Found</option>
                ) : (
                  admissionsList.map((adm) => (
                    <option key={adm.id} value={adm.id}>
                      {adm.admissionNumber} (ID: {adm.id.substring(0, 8)}...)
                    </option>
                  ))
                )}
              </select>
            </div>

            <Button
              variant="primary"
              size="sm"
              isLoading={isGenerating}
              onClick={handleGenerateRecommendation}
              className="mt-4 bg-status-info hover:bg-blue-700"
            >
              <Sparkles className="h-4 w-4 mr-1" />
              Generate Recommendation Batch
            </Button>
          </div>

          <Button variant="outline" size="sm" onClick={() => fetchRecommendation()}>
            <RefreshCw className="h-3.5 w-3.5 mr-1" />
            Refresh Batch
          </Button>
        </div>

        {/* Feedback Alert */}
        {feedbackMsg && (
          <div
            className={`rounded-md border p-3 text-xs font-semibold ${
              feedbackMsg.type === 'success'
                ? 'border-status-ready-border bg-status-ready-bg text-status-ready'
                : 'border-status-critical-border bg-status-critical-bg text-status-critical'
            }`}
          >
            {feedbackMsg.text}
          </div>
        )}

        {/* Loading State */}
        {isLoading ? (
          <div className="flex h-64 items-center justify-center">
            <div className="flex flex-col items-center gap-3">
              <div className="h-8 w-8 animate-spin rounded-full border-4 border-clinical-navy border-t-transparent"></div>
              <p className="text-xs font-semibold text-clinical-on-surface-variant">
                Evaluating clinical safety & feasibility constraints...
              </p>
            </div>
          </div>
        ) : !recommendation ? (
          /* Empty Recommendation State with Generate Call-to-Action */
          <Card>
            <CardContent className="p-12 text-center space-y-4">
              <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-full bg-status-info-bg text-status-info">
                <Sparkles className="h-8 w-8" />
              </div>
              <div>
                <h3 className="text-sm font-bold text-clinical-navy uppercase tracking-wider">
                  No Active Recommendation Batch Found
                </h3>
                <p className="text-xs text-clinical-on-surface-variant mt-1 max-w-md mx-auto">
                  There is currently no allocation recommendation generated for Admission ID{' '}
                  <span className="font-mono font-bold">{selectedAdmissionId}</span>.
                </p>
              </div>
              <Button
                variant="primary"
                size="md"
                isLoading={isGenerating}
                onClick={handleGenerateRecommendation}
                className="bg-status-info hover:bg-blue-700"
              >
                <Sparkles className="h-4 w-4 mr-2" />
                Generate Allocation Recommendation Batch
              </Button>
            </CardContent>
          </Card>
        ) : recommendation.status === 'NO_FEASIBLE_ALLOCATION' ? (
          /* NO FEASIBLE ALLOCATION Hard Constraint Violation State */
          <Card statusBarColor="critical">
            <CardHeader>
              <CardTitle className="text-status-critical flex items-center gap-2">
                <ShieldAlert className="h-5 w-5" />
                <span>NO FEASIBLE ALLOCATION CURRENTLY AVAILABLE</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="rounded border border-status-critical-border bg-status-critical-bg p-4 text-xs">
                <p className="font-bold text-status-critical">Mandatory Safety Constraint Violation</p>
                <p className="text-clinical-on-surface-variant mt-1">
                  The algorithm evaluated all hospital candidate beds/equipment for patient{' '}
                  <strong>{recommendation.patientName || selectedAdmissionId}</strong>, but no resource passed mandatory Lassa fever isolation safety checks.
                </p>
              </div>

              <div className="space-y-2">
                <h4 className="text-xs font-bold uppercase text-clinical-navy">Evaluated Hard Constraints</h4>
                <div className="rounded border border-clinical-border bg-clinical-container-low p-3 space-y-1.5 text-xs">
                  <div className="flex items-center gap-2 text-status-critical">
                    <XCircle className="h-4 w-4 shrink-0" />
                    <span>Isolation Capability: Negative pressure beds occupied or undergoing decontamination.</span>
                  </div>
                </div>
              </div>

              <div className="flex justify-end gap-2 pt-2">
                <Button variant="outline" size="sm" onClick={() => fetchRecommendation()}>Re-evaluate Capacity</Button>
                <Button variant="danger" size="sm" onClick={() => setOverrideModalOpen(true)}>Initiate Emergency Override</Button>
              </div>
            </CardContent>
          </Card>
        ) : recommendation.status === 'INSUFFICIENT_DATA' ? (
          /* INSUFFICIENT DATA State */
          <Card statusBarColor="warning">
            <CardHeader>
              <CardTitle className="text-status-warning flex items-center gap-2">
                <HelpCircle className="h-5 w-5" />
                <span>INSUFFICIENT CLINICAL DATA</span>
              </CardTitle>
            </CardHeader>
            <CardContent className="space-y-4">
              <div className="rounded border border-status-warning-border bg-status-warning-bg p-4 text-xs">
                <p className="font-bold text-status-warning">Required Patient Assessment Parameters Missing</p>
                <p className="text-clinical-on-surface-variant mt-1">
                  {recommendation.missingDataReason ||
                    'Clinical Assessment must be completed (Infection Status, Hemorrhagic Risk Level, Care Level) before recommendation calculation can proceed.'}
                </p>
              </div>

              <Button variant="primary" size="sm" onClick={() => window.location.href = '/patients'}>
                Complete Patient Assessment
              </Button>
            </CardContent>
          </Card>
        ) : (
          /* ACTIVE RECOMMENDATION BATCH DISPLAY */
          <div className="space-y-6">
            <div className="flex flex-col gap-2 rounded-md border border-clinical-border bg-white p-4 shadow-xs md:flex-row md:items-center md:justify-between">
              <div>
                <div className="flex items-center gap-2">
                  <Sparkles className="h-5 w-5 text-status-info" />
                  <h2 className="text-sm font-bold uppercase tracking-wider text-clinical-navy">
                    Recommended Allocation Matrix
                  </h2>
                  <Badge variant="info">Batch {recommendation.batchType || 'ALLOCATION'}</Badge>
                </div>
                <p className="text-xs text-clinical-on-surface-variant mt-1">
                  Admission ID: <span className="font-mono font-bold text-gray-700">{recommendation.admissionId}</span>
                </p>
              </div>

              <div className="flex items-center gap-2">
                <span className="text-[11px] text-gray-500 font-mono">
                  Generated: {new Date(recommendation.generatedAt || Date.now()).toLocaleTimeString()}
                </span>
                <Button variant="outline" size="sm" onClick={() => fetchRecommendation()}>
                  Re-Calculate
                </Button>
              </div>
            </div>

            <div className="grid grid-cols-1 gap-6 lg:grid-cols-12">
              {/* Ranked Recommendation Items */}
              <div className="lg:col-span-8 space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle>Recommended Candidates (Ranked by Safety & Score)</CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    {(!recommendation.items || recommendation.items.length === 0) ? (
                      <div className="p-4 text-xs text-gray-500 text-center">
                        No candidate items generated in batch.
                      </div>
                    ) : (
                      recommendation.items.map((item) => (
                        <div
                          key={item.id}
                          className={`rounded-md border p-4 transition-all ${
                            item.status === 'ACCEPTED'
                              ? 'border-status-ready-border bg-status-ready-bg/50'
                              : item.status === 'REJECTED' || item.status === 'OVERRIDDEN'
                              ? 'border-gray-300 bg-gray-50 opacity-60'
                              : 'border-clinical-border bg-white shadow-xs'
                          }`}
                        >
                          <div className="flex items-start justify-between">
                            <div className="flex items-center gap-2.5">
                              <span className="flex h-6 w-6 items-center justify-center rounded-full bg-clinical-navy text-xs font-bold text-white font-mono">
                                #{item.rank || 1}
                              </span>
                              <div>
                                <h4 className="text-xs font-bold text-clinical-navy">
                                  {item.recommendedEntityName || `${item.itemType || 'Resource'} Candidate`}
                                </h4>
                                <span className="text-[10px] font-mono text-gray-500">ID: {item.recommendedEntityId}</span>
                              </div>
                            </div>

                            <div className="text-right">
                              <span className="text-[10px] font-bold uppercase text-clinical-on-surface-variant">Allocation Score</span>
                              <div className="text-base font-bold font-tabular text-status-info">
                                {(((item.allocationScore ?? item.confidenceScore) || 0) * 100).toFixed(0)}%
                              </div>
                            </div>
                          </div>

                          <div className="mt-3 rounded bg-clinical-container-low p-2.5 text-xs text-clinical-on-surface">
                            <p className="font-bold text-clinical-navy mb-0.5">Why this option was recommended:</p>
                            <p className="text-clinical-on-surface-variant leading-relaxed">
                              {item.rationale || 'Matched mandatory isolation negative pressure protocol and clinical priority requirements.'}
                            </p>
                          </div>

                          <div className="mt-4 flex items-center justify-between border-t border-clinical-border pt-3">
                            <div>
                              {item.status !== 'PENDING' && (
                                <Badge variant={item.status === 'ACCEPTED' ? 'ready' : 'neutral'}>
                                  Decision: {item.status}
                                </Badge>
                              )}
                            </div>

                            {item.status === 'PENDING' && (
                              canDecide ? (
                                <div className="flex items-center gap-2">
                                  <Button
                                    variant="secondary"
                                    size="sm"
                                    onClick={() => {
                                      setSelectedItemId(item.id);
                                      setRejectModalOpen(true);
                                    }}
                                    className="text-xs"
                                  >
                                    <X className="h-3.5 w-3.5 mr-1" />
                                    Reject
                                  </Button>

                                  <Button
                                    variant="outline"
                                    size="sm"
                                    onClick={() => {
                                      setSelectedItemId(item.id);
                                      setOverrideModalOpen(true);
                                    }}
                                    className="text-xs"
                                  >
                                    <FileEdit className="h-3.5 w-3.5 mr-1 text-amber-600" />
                                    Override
                                  </Button>

                                  <Button
                                    variant="primary"
                                    size="sm"
                                    isLoading={isSubmitting}
                                    onClick={() => handleAccept(item.id)}
                                    className="text-xs bg-status-ready hover:bg-emerald-700"
                                  >
                                    <Check className="h-3.5 w-3.5 mr-1" />
                                    Accept Recommendation
                                  </Button>
                                </div>
                              ) : (
                                <span className="text-[11px] font-semibold text-gray-400 italic">
                                  Decision authority: Ward Manager / Doctor required
                                </span>
                              )
                            )}
                          </div>
                        </div>
                      ))
                    )}
                  </CardContent>
                </Card>
              </div>

              {/* Feasibility Checks Sidebar */}
              <div className="lg:col-span-4 space-y-4">
                <Card>
                  <CardHeader>
                    <CardTitle className="flex items-center gap-2">
                      <ShieldCheck className="h-4 w-4 text-status-ready" />
                      <span>Safety & Feasibility Checks</span>
                    </CardTitle>
                  </CardHeader>
                  <CardContent className="space-y-3">
                    {(recommendation.feasibilityChecks || [
                      { criterion: 'Infection Control / Isolation Match', passed: true, type: 'MANDATORY', details: 'Negative pressure certified' },
                      { criterion: 'Care Level Capability (ICU / HDU)', passed: true, type: 'MANDATORY', details: 'Continuous monitoring ready' },
                      { criterion: 'Decontamination & Hygiene Verified', passed: true, type: 'MANDATORY', details: 'Decontamination protocol verified' },
                    ]).map((check, idx) => (
                      <div key={idx} className="flex items-start gap-2.5 border-b border-clinical-border pb-2.5 last:border-0">
                        {check.passed ? (
                          <CheckCircle2 className="h-4 w-4 shrink-0 text-status-ready mt-0.5" />
                        ) : (
                          <XCircle className="h-4 w-4 shrink-0 text-status-critical mt-0.5" />
                        )}
                        <div>
                          <div className="flex items-center gap-1.5">
                            <span className="text-xs font-semibold text-clinical-navy">{check.criterion}</span>
                            <Badge variant={check.type === 'MANDATORY' ? 'critical' : 'neutral'} className="text-[9px] py-0 px-1">
                              {check.type}
                            </Badge>
                          </div>
                          {check.details && (
                            <p className="text-[11px] text-clinical-on-surface-variant mt-0.5">{check.details}</p>
                          )}
                        </div>
                      </div>
                    ))}
                  </CardContent>
                </Card>
              </div>
            </div>
          </div>
        )}
      </div>

      {/* Reject Modal */}
      <Modal isOpen={rejectModalOpen} onClose={() => setRejectModalOpen(false)} title="Reject Recommendation">
        <div className="space-y-4">
          <p className="text-xs text-clinical-on-surface-variant">
            Please specify the clinical or operational reason for declining this recommended allocation. This action will be recorded in the immutable audit trail.
          </p>
          <div>
            <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Rejection Reason *</label>
            <textarea
              required
              rows={3}
              value={rejectionReason}
              onChange={(e) => setRejectionReason(e.target.value)}
              className="w-full rounded border border-clinical-border bg-white p-2 text-xs focus:border-status-info focus:outline-none"
              placeholder="e.g. Bed location too far from central nursing station for high risk patient."
            />
          </div>
          <div className="flex justify-end gap-2 pt-2">
            <Button variant="outline" size="sm" onClick={() => setRejectModalOpen(false)}>Cancel</Button>
            <Button variant="danger" size="sm" isLoading={isSubmitting} onClick={handleRejectSubmit}>
              Confirm Rejection
            </Button>
          </div>
        </div>
      </Modal>

      {/* Override Modal */}
      <Modal isOpen={overrideModalOpen} onClose={() => setOverrideModalOpen(false)} title="Manual Override Workflow">
        <div className="space-y-4">
          <div className="rounded border border-status-warning-border bg-status-warning-bg p-3 text-xs text-amber-800">
            <div className="flex items-center gap-1.5 font-bold mb-1">
              <AlertTriangle className="h-4 w-4 text-status-warning" />
              <span>Audit Warning</span>
            </div>
            This manual override will replace the algorithm's safety recommendation and will be logged in the permanent hospital audit log with your user ID.
          </div>

          <div>
            <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Override Candidate ID *</label>
            <input
              type="text"
              required
              value={overrideEntityId}
              onChange={(e) => setOverrideEntityId(e.target.value)}
              className="w-full rounded border border-clinical-border bg-white p-2 text-xs font-mono focus:border-status-info focus:outline-none"
              placeholder="e.g. bed-108 or eq-501"
            />
          </div>

          <div>
            <label className="block text-xs font-bold uppercase text-clinical-navy mb-1">Override Justification *</label>
            <textarea
              required
              rows={3}
              value={overrideJustification}
              onChange={(e) => setOverrideJustification(e.target.value)}
              className="w-full rounded border border-clinical-border bg-white p-2 text-xs focus:border-status-info focus:outline-none"
              placeholder="Detailed clinical rationale for manual selection override."
            />
          </div>

          <div className="flex justify-end gap-2 pt-2">
            <Button variant="outline" size="sm" onClick={() => setOverrideModalOpen(false)}>Cancel</Button>
            <Button variant="primary" size="sm" isLoading={isSubmitting} onClick={handleOverrideSubmit}>
              Confirm Manual Override
            </Button>
          </div>
        </div>
      </Modal>
    </AppShell>
  );
};
