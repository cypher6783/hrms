# Staff Module Plan – Phase 3

## Overview

The Staff module manages hospital staff records, availability, specializations, department assignments, and workload calculations.

## Module Dependencies

- **Ward** (Phase 2) – department assignment
- **Admission** (Phase 3) – workload calculation from active admissions
- **Shift Management** (Phase 3) – shift assignment integration

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `StaffController` | REST endpoints, request validation |
| Application Service | `StaffApplicationService` | Use-case orchestration, CRUD operations |
| Domain Service | `WorkloadCalculator` | Workload calculation logic |
| Repository | `StaffRepository` | Data access, JPA queries |
| Mapper | `StaffMapper` | Entity-DTO conversion (MapStruct) |
| Events | `StaffEventPublisher` | Domain event publication |

### Entities

**Staff** (existing, enhanced)
- UUID primary key
- Staff number (auto-generated: STF-XXXXXXXX)
- Full name, role, specialization
- Certification status and expiry
- Ward ID (FK reference)
- Max workload threshold
- Availability status: ACTIVE, INACTIVE, ON_LEAVE
- Audit: createdAt, updatedAt, createdBy, updatedBy

### DTOs

**Request DTOs:**
- `StaffRequest` – fullName, role, specialization, certificationStatus, certificationExpiry, wardId, maxWorkloadThreshold, availabilityStatus
- `StaffSearchRequest` – name, role, specialization, wardId, availabilityStatus, certificationStatus, page, size

**Response DTOs:**
- `StaffResponse` – full staff details
- `StaffSummaryResponse` – id, staffNumber, fullName, role, specialization, wardId, availabilityStatus
- `StaffWorkloadResponse` – staffId, currentWorkload, maxThreshold, workloadPercentage, activeAdmissions, severityFactors

### Business Rules

1. Staff number is unique and auto-generated
2. Workload is calculated from active admissions, patient severity, shift duration, and isolation assignments
3. Workload cannot exceed maxWorkloadThreshold
4. Certification expiry is checked for validity
5. Staff availability affects shift assignment eligibility

### Workload Calculation

Workload formula:
```
workload = (activeAdmissions × severityWeight) + (isolationAssignments × isolationWeight)
workloadPercentage = (workload / maxWorkloadThreshold) × 100
```

Severity weights:
- CRITICAL: 1.5
- HIGH: 1.2
- MODERATE: 1.0
- LOW: 0.8

### Domain Events

- `StaffAssigned` – published when staff is assigned to a ward or shift

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/staff` | Create staff member |
| GET | `/api/v1/staff/{id}` | Get staff by ID |
| GET | `/api/v1/staff` | Search staff (paginated) |
| PUT | `/api/v1/staff/{id}` | Update staff |
| GET | `/api/v1/staff/ward/{wardId}` | Get staff by ward |
| GET | `/api/v1/staff/{id}/workload` | Get staff workload |
| GET | `/api/v1/staff/stats` | Get staff statistics |
