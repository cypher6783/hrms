# Equipment Module Plan – Phase 4

## Overview

The Equipment Management module tracks medical device assets, serial numbers, operational statuses, location assignments, utilization history, and calibration condition monitoring.

## Module Dependencies

- **Ward** (Phase 2) – ward location assignment
- **Admission** (Phase 3) – patient assignment context
- **Maintenance** (Phase 4) – maintenance downtime tracking

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `EquipmentController` | REST endpoints for equipment CRUD, assignment, release, and queries |
| Application Service | `EquipmentApplicationService` | Orchestrates asset lifecycle, ward/admission assignments, returns |
| Domain Service | `EquipmentDomainService` | Validates status transitions, double-assignment restrictions, maintenance locks |
| Repository | `EquipmentRepository`, `EquipmentAllocationRepository` | JPA persistence, custom queries, specifications |
| Mapper | `EquipmentMapper`, `AllocationMapper` | MapStruct entity-DTO mappings |
| Events | `DomainEventPublisher` | Publishes `EquipmentAssignedEvent`, `EquipmentReleasedEvent` |

### Entities

**Equipment**
- UUID primary key (`id`)
- `name`: String
- `equipmentType`: String / Enum (`VENTILATOR`, `MONITOR`, `INFUSION_PUMP`, `DEFIBRILLATOR`, `ULTRASOUND`, `DIAGNOSTIC`, `GENERAL`)
- `serialNumber`: String (unique)
- `location`: String
- `status`: String (`AVAILABLE`, `IN_USE`, `UNDER_MAINTENANCE`, `DECONTAMINATION_REQUIRED`, `OUT_OF_SERVICE`)
- `assignedAdmissionId`: UUID FK (nullable)
- `assignedWardId`: UUID FK (nullable)
- Timestamps: `createdAt`, `updatedAt`, `createdBy`, `updatedBy`

**EquipmentAllocation**
- UUID primary key (`id`)
- `equipmentId`: UUID FK
- `admissionId`: UUID FK
- `allocatedAt`: Instant
- `releasedAt`: Instant (nullable)
- `allocatedBy`: UUID FK

### Business Rules

1. **Single Simultaneous Assignment**: Equipment cannot be assigned to more than one admission or ward simultaneously.
2. **Maintenance Lock**: Equipment under maintenance or out of service cannot be allocated or assigned.
3. **Immutable History**: Equipment allocation and usage records are append-only.

### Domain Events

- `EquipmentAssignedEvent` – published when equipment is assigned to an admission/ward
- `EquipmentReleasedEvent` – published when equipment is returned/released
