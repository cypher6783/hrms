# Phase 4 Testing Checklist

## Repository Layer Tests

- [x] `ResourceRepositoryTest`
  - Category filtering query specs
  - Threshold queries (minimum threshold, reorder point)
  - Custom JPQL/Specification searches
  - Pagination and sorting verification
- [x] `ResourceInventoryRepositoryTest`
  - Sum stock by resource ID
  - Location and batch unique constraint checks
  - Expired batch filtering queries
- [x] `EquipmentRepositoryTest`
  - Serial number uniqueness check
  - Status and ward assignment query specs
  - Available equipment queries
- [x] `EquipmentMaintenanceRepositoryTest`
  - Equipment maintenance log retrieval
  - Overdue scheduled maintenance queries

---

## Domain Service Layer Tests

- [x] `ResourceDomainServiceTest`
  - Name uniqueness within category enforcement
  - Double allocation & double reservation prevention
  - Immediate availability upon release
- [x] `InventoryDomainServiceTest`
  - Non-negative stock invariant checks
  - Expired batch allocation prevention
  - Immutable stock transaction ledger checks
- [x] `EquipmentDomainServiceTest`
  - Single simultaneous assignment rule
  - Maintenance lock enforcement (cannot assign under-maintenance asset)
  - Allocation history immutability
- [x] `MaintenanceDomainServiceTest`
  - Auto-locking equipment to `UNDER_MAINTENANCE` on maintenance schedule
  - Return-to-service verification requirement before reuse
  - Maintenance history immutability

---

## Application Service Layer Tests

- [x] `ResourceApplicationServiceTest`
  - Success path: resource registration, reservation, allocation, release
  - Failure path: resource not found, insufficient stock, double allocation
  - Utilization metric calculations
- [x] `InventoryApplicationServiceTest`
  - Stock transaction processing (`IN`, `OUT`, `ADJUSTMENT`)
  - Low stock notification triggering
  - Supplier CRUD operations
- [x] `EquipmentApplicationServiceTest`
  - Equipment registration, assignment, return
  - Unavailable equipment assignment rejection
- [x] `EquipmentMaintenanceApplicationServiceTest`
  - Maintenance scheduling, completion, verification workflow

---

## Controller Layer Tests

- [x] `ResourceControllerTest`
  - POST `/api/v1/resources` (create, validation)
  - GET `/api/v1/resources` (list, filter by category)
  - POST `/api/v1/resources/reservations` & `/allocations`
- [x] `InventoryControllerTest`
  - POST `/api/v1/inventory/transactions` (record movement, validation)
  - GET `/api/v1/inventory/stock/{resourceId}`
- [x] `EquipmentControllerTest`
  - POST `/api/v1/equipment` (register asset)
  - POST `/api/v1/equipment/{id}/assign` & `/release`
- [x] `EquipmentMaintenanceControllerTest`
  - POST `/api/v1/equipment/{equipmentId}/maintenance`
  - POST `/api/v1/equipment/{equipmentId}/maintenance/{id}/complete`
  - POST `/api/v1/equipment/{equipmentId}/maintenance/{id}/verify`

---

## End-to-End Integration Tests

- [x] `ResourceAllocationIntegrationTest`: Full resource reservation -> allocation -> inventory deduction -> release workflow.
- [x] `InventoryMovementIntegrationTest`: Stock receipt -> batch tracking -> low stock detection -> expiry writeoff.
- [x] `EquipmentAssignmentIntegrationTest`: Asset registration -> assignment to admission -> return to service.
- [x] `MaintenanceWorkflowIntegrationTest`: Equipment scheduling -> status lock -> completion -> supervisor verification -> return to service.
