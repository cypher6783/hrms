# Phase 4 – Resources Checklist

## Module 1: Resource Management
- [x] Entity `Resource` implemented with UUID PK, audit fields, thresholds, criticality levels.
- [x] Enum / Category `ResourceCategory` implemented.
- [x] Entity `ResourceAllocation` implemented with JPA mapping to `resource_allocations`.
- [x] Entity `ResourceReservation` implemented with JPA mapping to `resource_reservations`.
- [x] `ResourceRepository`, `ResourceAllocationRepository`, `ResourceReservationRepository` implemented with pagination and specifications.
- [x] DTOs separated (`ResourceRequest`, `ResourceResponse`, `ResourceAllocationRequest`, `ResourceAllocationResponse`, `ResourceReservationRequest`, `ResourceReservationResponse`, `ResourceUtilizationResponse`).
- [x] MapStruct `ResourceMapper`, `AllocationMapper`, `ReservationMapper` implemented.
- [x] `ResourceDomainService` enforcing uniqueness, double-allocation prevention, and immediate release availability.
- [x] `ResourceApplicationService` orchestrating registration, reservation, allocation, release, and utilization calculations.
- [x] `ResourceController` REST endpoints implemented with `ApiResponse<T>` wrappers.
- [x] Domain Events `ResourceReservedEvent`, `ResourceAllocatedEvent`, `ResourceReleasedEvent` implemented and published.

## Module 2: Inventory Management
- [x] Entity `ResourceInventory` updated with batch tracking and expiration dates.
- [x] Entity `InventoryTransaction` updated with transaction types, performedBy, and audit fields.
- [x] Entity `ResourceSupplier` updated with lead time and status.
- [x] `ResourceInventoryRepository`, `InventoryTransactionRepository`, `ResourceSupplierRepository` implemented with queries and specifications.
- [x] DTOs separated (`InventoryTransactionRequest`, `InventoryTransactionResponse`, `InventoryStockResponse`, `SupplierRequest`, `SupplierResponse`).
- [x] MapStruct `InventoryMapper`, `SupplierMapper` implemented.
- [x] `InventoryDomainService` enforcing non-negative stock invariants and FEFO expiration shielding.
- [x] `InventoryApplicationService` handling transactions, stock adjustments, low-stock detection, and supplier management.
- [x] `InventoryController` and `SupplierController` REST endpoints implemented with `ApiResponse<T>`.
- [x] Domain Events `InventoryUpdatedEvent`, `InventoryLowStockEvent`, `InventoryExpiredEvent` implemented and published.

## Module 3: Equipment Management
- [x] Entity `Equipment` updated with serial number, location, status, ward/admission FKs.
- [x] Enum `EquipmentCategory` implemented.
- [x] Entity `EquipmentAllocation` implemented with JPA mapping to `equipment_allocations`.
- [x] `EquipmentRepository`, `EquipmentAllocationRepository` implemented with queries and specifications.
- [x] DTOs separated (`EquipmentRequest`, `EquipmentResponse`, `EquipmentAllocationRequest`, `EquipmentAllocationResponse`, `EquipmentUsageHistoryResponse`).
- [x] MapStruct `EquipmentMapper` implemented.
- [x] `EquipmentDomainService` enforcing single simultaneous assignment and maintenance locks.
- [x] `EquipmentApplicationService` handling registration, assignment, return, and utilization history.
- [x] `EquipmentController` REST endpoints implemented with `ApiResponse<T>`.
- [x] Domain Events `EquipmentAssignedEvent`, `EquipmentReleasedEvent` implemented and published.

## Module 4: Maintenance Management
- [x] Entity `EquipmentMaintenance` updated with types, statuses, costs, and scheduled/completed dates.
- [x] `EquipmentMaintenanceRepository` implemented with specifications for scheduled and overdue items.
- [x] DTOs separated (`MaintenanceRequest`, `MaintenanceResponse`).
- [x] MapStruct `MaintenanceMapper` implemented.
- [x] `MaintenanceDomainService` enforcing downtime tracking and return-to-service verification workflows.
- [x] `EquipmentMaintenanceApplicationService` handling preventive/corrective maintenance, completions, and return-to-service.
- [x] `EquipmentMaintenanceController` REST endpoints implemented with `ApiResponse<T>`.
- [x] Domain Events `EquipmentMaintenanceStartedEvent`, `EquipmentMaintenanceCompletedEvent`, `EquipmentReturnedToServiceEvent` implemented and published.

## Testing & Quality
- [x] Repository unit tests implemented for specifications, custom queries, and pagination.
- [x] Domain service tests implemented for business rule enforcement.
- [x] Application service tests implemented for success and failure flows.
- [x] Controller tests implemented for REST contracts and validation.
- [x] Integration tests implemented for multi-step resource allocation, inventory, equipment, and maintenance workflows.
