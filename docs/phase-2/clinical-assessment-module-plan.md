# Clinical Assessment Module Plan

## [S1] Entity

**Package**: `com.hospital.resource.assessment.entity`

**Table**: `clinical_assessments`

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, auto-generated | |
| patient_id | UUID | NOT NULL, FK | References patients.id |
| admission_id | UUID | | References admissions.id |
| assessed_by | UUID | NOT NULL | Staff who performed assessment |
| severity_level | String(20) | NOT NULL | CRITICAL, HIGH, MEDIUM, LOW |
| triage_classification | String(20) | NOT NULL | IMMEDIATE, URGENT, DELAYED, MINOR |
| infection_status | String(20) | NOT NULL | ISOLATION_REQUIRED, PRECAUTIONS, NONE |
| clinical_notes | TEXT | | Free-form notes |
| is_reassessment | Boolean | NOT NULL, default false | |
| assessment_timestamp | Instant | NOT NULL | When assessment occurred |
| created_at | Instant | NOT NULL, auto-set | Audit field |

**Important**: Entity has `@PrePersist` only. No `@PreUpdate` — assessment is append-only.

## [S2] Append-Only Design

**ClinicalAssessment is immutable after creation.**

- No update operations in service layer
- No update endpoint in controller
- Entity has no `updatedAt` or `updatedBy` fields
- Reassessment = new record with `isReassessment = true`

## [S3] Repository

**Interface**: `ClinicalAssessmentRepository extends JpaRepository<ClinicalAssessment, UUID>`

Custom queries:
- `findByPatientIdOrderByAssessmentTimestampDesc(UUID patientId)` → List (timeline)
- `findByAdmissionIdOrderByAssessmentTimestampDesc(UUID admissionId)` → List (admission timeline)
- `findTopByAdmissionIdOrderByAssessmentTimestampDesc(UUID admissionId)` → ClinicalAssessment (latest)

## [S4] DTOs

**ClinicalAssessmentRequest**:
- patientId: @NotNull UUID
- admissionId: UUID (optional)
- severityLevel: @NotBlank
- triageClassification: @NotBlank
- infectionStatus: @NotBlank
- clinicalNotes: String

**ClinicalAssessmentResponse**:
- All entity fields except assessedBy → assessedByStaffId

**ClinicalAssessmentSummaryResponse**:
- id, patientId, severityLevel, triageClassification, assessmentTimestamp

## [S5] Mapper

**Interface**: `ClinicalAssessmentMapper` (MapStruct)

Methods:
- `toEntity(ClinicalAssessmentRequest)` → ClinicalAssessment
- `toResponse(ClinicalAssessment)` → ClinicalAssessmentResponse
- `toSummary(ClinicalAssessment)` → ClinicalAssessmentSummaryResponse
- `toResponseList(List<ClinicalAssessment>)` → List<ClinicalAssessmentResponse>

## [S6] Service

**Application Service**: `ClinicalAssessmentApplicationService`

Operations:
| Operation | Method | Transaction | Notes |
|-----------|--------|-------------|-------|
| Create assessment | createAssessment | @Transactional | Sets isReassessment flag |
| Patient timeline | getPatientTimeline | readOnly | Ordered by timestamp desc |
| Admission timeline | getAdmissionTimeline | readOnly | |
| Latest by admission | getLatestByAdmission | readOnly | Throws if not found |

**Business rules**:
- Assessment timestamp auto-set to Instant.now()
- isReassessment determined by existing assessments for admission
- NO update operation (append-only)

## [S7] Controller

**Base path**: `/api/v1/assessments`

| Method | Path | Description |
|--------|------|-------------|
| POST | / | Create assessment |
| GET | /patient/{patientId} | Patient timeline |
| GET | /admission/{admissionId} | Admission timeline |
| GET | /admission/{admissionId}/latest | Latest assessment for admission |

**No PUT or DELETE endpoints** — append-only design.

## [S8] Timeline Queries

- Patient timeline: all assessments for a patient, ordered by timestamp descending
- Admission timeline: all assessments for an admission
- Latest assessment: most recent assessment for an admission

## [S9] Testing

- Repository tests: timeline queries, latest query
- Service tests: create assessment, timeline retrieval, append-only enforcement
- Controller tests: endpoint validation, no update/delete endpoints
