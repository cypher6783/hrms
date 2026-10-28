# Admission Module Plan – Phase 3

## Overview

The Admission module manages the complete lifecycle of patient admissions from admission through discharge, including ward/bed assignment, transfers, and discharge workflows.

## Module Dependencies

- **Patient** (Phase 2) – patient identification
- **Ward** (Phase 2) – ward assignment
- **Bed** (Phase 2) – bed assignment and status management
- **Bed Cleaning** (Phase 3) – auto-created on discharge/transfer
- **Clinical Assessment** (Phase 2) – admission severity context

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `AdmissionController` | REST endpoints, request validation, response wrapping |
| Application Service | `AdmissionApplicationService` | Use-case orchestration, transaction management |
| Domain Service | `AdmissionDomainService` | Business rules, validation, lifecycle management |
| Repository | `AdmissionRepository` | Data access, JPA queries |
| Mapper | `AdmissionMapper` | Entity-DTO conversion (MapStruct) |
| Events | `AdmissionEventPublisher` | Domain event publication |

### Entities

**Admission** (existing, enhanced)
- UUID primary key
- Admission number (auto-generated: ADM-yyyyMMdd-NNNN)
- Patient ID (FK reference)
- Ward ID (FK reference)
- Bed ID (FK reference, nullable)
- Status: PENDING → ADMITTED → ACTIVE → DISCHARGED
- Admission notes, discharge outcome, discharge notes
- Timestamps: admittedAt, dischargedAt, createdAt, updatedAt
- Audit: createdBy, updatedBy
- Soft delete: isActive flag

### DTOs

**Request DTOs:**
- `AdmissionRequest` – patientId, wardId, bedId, admissionNotes
- `TransferRequest` – newWardId, newBedId, transferNotes
- `DischargeRequest` – dischargeOutcome, dischargeNotes
- `AdmissionSearchRequest` – patientId, wardId, status, dateFrom, dateTo, page, size

**Response DTOs:**
- `AdmissionResponse` – full admission details
- `AdmissionSummaryResponse` – id, admissionNumber, patientId, status, wardId, admittedAt

### Business Rules

1. One active admission per patient at any time
2. Patient cannot be assigned to an occupied bed
3. Bed status must update automatically on admission events
4. Admission timestamps (admittedAt) are immutable after creation
5. Transfer releases old bed and creates cleaning task
6. Discharge releases bed, creates cleaning task, marks admission inactive
7. Admission number is unique and auto-generated

### Operational Workflow

```
Admit Patient → Assign Ward → Assign Bed → [Clinical Care] → Transfer (optional) → Discharge → Cleaning Task Created
```

### Domain Events

- `AdmissionCreated` – published on new admission
- `AdmissionTransferred` – published on transfer
- `AdmissionDischarged` – published on discharge
- `BedAssigned` – published when bed is assigned to admission
- `BedReleased` – published when bed is released from admission

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/admissions` | Create admission |
| GET | `/api/v1/admissions/{id}` | Get admission by ID |
| GET | `/api/v1/admissions` | Search admissions (paginated) |
| GET | `/api/v1/admissions/patient/{patientId}/active` | Get active admission for patient |
| PUT | `/api/v1/admissions/{id}/transfer` | Transfer patient |
| PUT | `/api/v1/admissions/{id}/discharge` | Discharge patient |
| GET | `/api/v1/admissions/ward/{wardId}` | Get admissions by ward |
| GET | `/api/v1/admissions/stats` | Get admission statistics |
