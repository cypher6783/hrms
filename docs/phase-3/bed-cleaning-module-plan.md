# Bed Cleaning Module Plan – Phase 3

## Overview

The Bed Cleaning module manages the lifecycle of bed cleaning tasks from creation through verification, ensuring beds are properly cleaned before becoming available for new patients.

## Module Dependencies

- **Bed** (Phase 2) – bed status management
- **Admission** (Phase 3) – cleaning task creation on discharge/transfer
- **Staff** (Phase 3) – cleaner assignment

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `BedCleaningController` | REST endpoints, request validation |
| Application Service | `BedCleaningApplicationService` | Use-case orchestration, transaction management |
| Domain Service | `BedCleaningDomainService` | Business rules, status transition validation |
| Repository | `BedCleaningRepository` | Data access, JPA queries |
| Mapper | `BedCleaningMapper` | Entity-DTO conversion (MapStruct) |
| Events | `BedCleaningEventPublisher` | Domain event publication |

### Entities

**BedCleaning** (existing, enhanced)
- UUID primary key
- Bed ID (FK reference)
- Admission ID (FK reference)
- Status: PENDING → ASSIGNED → IN_PROGRESS → COMPLETED → VERIFIED
- Assigned staff (UUID), assignment timestamp
- Started/completed timestamps
- Verified by (UUID), verification timestamp
- Cleaning notes
- Audit: createdAt, updatedAt

### DTOs

**Request DTOs:**
- `CleaningAssignmentRequest` – staffId
- `CleaningCompletionRequest` – cleaningNotes
- `CleaningSearchRequest` – bedId, status, assignedTo, dateFrom, dateTo, page, size

**Response DTOs:**
- `CleaningTaskResponse` – full task details
- `CleaningTaskSummaryResponse` – id, bedId, status, assignedTo, createdAt

### Business Rules

1. Cleaning task is created automatically after discharge or transfer
2. Bed cannot become AVAILABLE until cleaning is verified
3. Cleaning history is immutable (timestamps cannot be changed)
4. Status transitions are strictly enforced: PENDING → ASSIGNED → IN_PROGRESS → COMPLETED → VERIFIED
5. Only verified beds return to AVAILABLE status

### Cleaning Lifecycle

```
Discharge/Transfer → PENDING → ASSIGNED → IN_PROGRESS → COMPLETED → VERIFIED → Bed AVAILABLE
```

### Domain Events

- `BedCleaningCreated` – published when task created
- `BedCleaningStarted` – published when cleaning begins
- `BedCleaningCompleted` – published when cleaning finished
- `BedCleaningVerified` – published when cleaning verified

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/bed-cleaning/pending` | Get pending tasks |
| GET | `/api/v1/bed-cleaning/bed/{bedId}` | Get tasks by bed |
| GET | `/api/v1/bed-cleaning` | Search tasks (paginated) |
| POST | `/api/v1/bed-cleaning/{id}/assign` | Assign cleaner |
| POST | `/api/v1/bed-cleaning/{id}/start` | Start cleaning |
| POST | `/api/v1/bed-cleaning/{id}/complete` | Complete cleaning |
| POST | `/api/v1/bed-cleaning/{id}/verify` | Verify cleaning |
| GET | `/api/v1/bed-cleaning/stats` | Get cleaning statistics |
