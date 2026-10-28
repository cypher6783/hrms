# Resource Module Plan – Phase 4

## Overview

The Resource Management module provides central management, reservation, allocation, release, and utilization tracking for consumable and non-consumable hospital resources.

## Module Dependencies

- **Ward** (Phase 2) – ward location and context
- **Admission** (Phase 3) – patient allocation context
- **Inventory** (Phase 4) – stock validation and updates
- **Supplier** (Phase 4) – procurement sourcing

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `ResourceController` | REST API endpoints, request validation, response formatting |
| Application Service | `ResourceApplicationService` | Orchestrates resource use cases, transaction demarcation |
| Domain Service | `ResourceDomainService` | Enforces resource availability, unique constraints, and allocation rules |
| Repository | `ResourceRepository`, `ResourceAllocationRepository`, `ResourceReservationRepository` | Data access and query specifications |
| Mapper | `ResourceMapper`, `AllocationMapper`, `ReservationMapper` | MapStruct entity-DTO conversions |
| Events | `DomainEventPublisher` | Publishes `ResourceReservedEvent`, `ResourceAllocatedEvent`, `ResourceReleasedEvent` |

### Entities

**Resource**
- UUID primary key (`id`)
- `name`: String (unique within category)
- `category`: Enum / String (`MEDICATION`, `CONSUMABLE`, `PPE`, `EQUIPMENT_SUPPLY`, `LAB_SUPPLY`, `GENERAL`)
- `unitOfMeasure`: String
- `minimumThreshold`: Integer
- `reorderPoint`: Integer
- `criticalityLevel`: String (`LOW`, `NORMAL`, `HIGH`, `CRITICAL`)
- `defaultSupplierId`: UUID FK
- Timestamps: `createdAt`, `updatedAt`, `createdBy`, `updatedBy`

**ResourceReservation**
- UUID primary key (`id`)
- `resourceId`: UUID FK
- `admissionId`: UUID FK (nullable)
- `quantity`: Integer
- `status`: String (`RESERVED`, `ALLOCATED`, `CANCELLED`, `EXPIRED`)
- `reservedAt`: Instant
- `expiresAt`: Instant
- `reservedBy`: UUID FK

**ResourceAllocation**
- UUID primary key (`id`)
- `resourceId`: UUID FK
- `admissionId`: UUID FK
- `quantity`: Integer
- `allocatedAt`: Instant
- `releasedAt`: Instant (nullable)
- `allocatedBy`: UUID FK

### DTOs

**Request DTOs:**
- `ResourceRequest` – name, category, unitOfMeasure, minimumThreshold, reorderPoint, criticalityLevel, defaultSupplierId
- `ResourceReservationRequest` – resourceId, admissionId, quantity, expirationMinutes
- `ResourceAllocationRequest` – resourceId, admissionId, quantity, reservationId

**Response DTOs:**
- `ResourceResponse` – resource metadata and supplier summary
- `ResourceReservationResponse` – reservation details and status
- `ResourceAllocationResponse` – allocation timestamps and patient context
- `ResourceUtilizationResponse` – utilization rate, total allocated, total reserved, available stock

### Business Rules

1. **Category Name Uniqueness**: Resource names must be unique within their category.
2. **Double-Allocation Prevention**: Reserved resources cannot be allocated twice or reserved beyond total available unreserved stock.
3. **Immediate Availability**: Released resources immediately return to available stock pools.
4. **Immutable History**: Allocation and reservation history records are append-only and immutable.

### Domain Events

- `ResourceReservedEvent` – published when resources are reserved
- `ResourceAllocatedEvent` – published when resources are allocated to an admission
- `ResourceReleasedEvent` – published when allocated resources are released
