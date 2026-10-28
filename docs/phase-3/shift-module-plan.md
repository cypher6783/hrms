# Shift Management Module Plan – Phase 3

## Overview

The Shift Management module handles the creation of hospital shifts, staff assignment to shifts, overlap prevention, staffing level calculation, and shift calendar management.

## Module Dependencies

- **Staff** (Phase 3) – staff assignment, availability checking
- **Ward** (Phase 2) – ward-specific shift scheduling

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `ShiftController` | REST endpoints, request validation |
| Application Service | `ShiftApplicationService` | Use-case orchestration |
| Domain Service | `ShiftDomainService` | Overlap prevention, staffing validation |
| Repository | `StaffShiftRepository`, `ShiftAssignmentRepository` | Data access |
| Mapper | `ShiftMapper`, `ShiftAssignmentMapper` | Entity-DTO conversion (MapStruct) |
| Events | `ShiftEventPublisher` | Domain event publication |

### Entities

**StaffShift** (existing, enhanced)
- UUID primary key
- Shift name, date, start/end times
- Ward ID (FK reference)
- Min required staff, max staff
- Status: SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED
- Audit fields

**ShiftAssignment** (existing, enhanced)
- UUID primary key
- Staff ID (FK reference)
- Shift ID (FK reference)
- Status: CONFIRMED, CANCELLED
- Assigned by (UUID)
- Unique constraint: (staff_id, shift_id)

### DTOs

**Request DTOs:**
- `ShiftRequest` – shiftName, shiftDate, startTime, endTime, wardId, minRequiredStaff, maxStaff
- `ShiftAssignmentRequest` – staffId, shiftId
- `ShiftSearchRequest` – wardId, shiftDateFrom, shiftDateTo, shiftName, status, page, size

**Response DTOs:**
- `ShiftResponse` – full shift details
- `ShiftSummaryResponse` – id, shiftName, shiftDate, startTime, endTime, wardId, status
- `ShiftAssignmentResponse` – full assignment details
- `StaffingLevelResponse` – shiftId, requiredStaff, assignedStaff, isFullyStaffed, deficit

### Business Rules

1. Staff cannot have overlapping shifts (same staff member on overlapping time ranges)
2. Shift assignments must respect staff availability status
3. Required staff-to-patient ratios must be validated
4. Shift capacity (maxStaff) cannot be exceeded
5. Staff cannot be assigned to the same shift twice

### Overlap Detection

Two shifts overlap when:
```
existing.startTime < new.endTime AND existing.endTime > new.startTime
```

### Staffing Level Calculation

```
assignedCount = count of CONFIRMED assignments for the shift
requiredCount = shift.minRequiredStaff
isFullyStaffed = assignedCount >= requiredCount
deficit = max(0, requiredCount - assignedCount)
```

### Domain Events

- `ShiftAssigned` – published when staff is assigned to a shift

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/v1/shifts` | Create shift |
| GET | `/api/v1/shifts/{id}` | Get shift by ID |
| GET | `/api/v1/shifts` | Search shifts (paginated) |
| GET | `/api/v1/shifts/ward/{wardId}/date/{date}` | Get shifts by ward and date |
| POST | `/api/v1/shifts/assign` | Assign staff to shift |
| DELETE | `/api/v1/shifts/assignments/{id}` | Remove shift assignment |
| GET | `/api/v1/shifts/{shiftId}/assignments` | Get shift assignments |
| GET | `/api/v1/shifts/{shiftId}/staffing-level` | Get staffing level |
| GET | `/api/v1/shifts/calendar` | Get shift calendar (date range) |
