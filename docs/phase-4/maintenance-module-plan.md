# Maintenance Module Plan – Phase 4

## Overview

The Maintenance Management module handles preventive and corrective maintenance scheduling, maintenance record logging, downtime tracking, and return-to-service verification.

## Module Dependencies

- **Equipment** (Phase 4) – target medical device asset
- **Staff** (Phase 3) / User – maintenance technician context

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `EquipmentMaintenanceController` | REST endpoints for scheduling, completing, verifying maintenance |
| Application Service | `EquipmentMaintenanceApplicationService` | Orchestrates maintenance workflows and return-to-service |
| Domain Service | `MaintenanceDomainService` | Enforces status transitions, verification checks, downtime calculations |
| Repository | `EquipmentMaintenanceRepository` | JPA persistence, overdue maintenance queries |
| Mapper | `MaintenanceMapper` | MapStruct entity-DTO mappings |
| Events | `DomainEventPublisher` | Publishes `EquipmentMaintenanceStartedEvent`, `EquipmentMaintenanceCompletedEvent`, `EquipmentReturnedToServiceEvent` |

### Entities

**EquipmentMaintenance**
- UUID primary key (`id`)
- `equipmentId`: UUID FK
- `maintenanceType`: String (`PREVENTIVE`, `CORRECTIVE`, `CALIBRATION`, `INSPECTION`)
- `status`: String (`SCHEDULED`, `IN_PROGRESS`, `COMPLETED`, `VERIFIED`, `OVERDUE`)
- `scheduledDate`: LocalDate
- `completedDate`: LocalDate (nullable)
- `performedBy`: String
- `maintenanceNotes`: String
- `cost`: BigDecimal
- `nextMaintenanceDate`: LocalDate (nullable)
- Timestamps: `createdAt`, `updatedAt`, `createdBy`, `updatedBy`

### Business Rules

1. **Maintenance Unavailable Lock**: Equipment entering scheduled/in-progress maintenance status is automatically set to `UNDER_MAINTENANCE` status.
2. **Return-to-Service Verification**: Completed maintenance requires explicit verification before the equipment is returned to `AVAILABLE` status.
3. **Immutable History**: Maintenance history log records cannot be updated or deleted once verified.

### Domain Events

- `EquipmentMaintenanceStartedEvent` – published when maintenance begins
- `EquipmentMaintenanceCompletedEvent` – published when technician completes work
- `EquipmentReturnedToServiceEvent` – published when maintenance is verified and equipment is restored to service
