# Inventory Module Plan – Phase 4

## Overview

The Inventory Management module maintains real-time stock levels, batch tracking, expiration management, supplier records, and an immutable audit ledger of all stock transactions.

## Module Dependencies

- **Resource** (Phase 4) – resource definitions and thresholds
- **Supplier** (Phase 4) – supplier information and lead times
- **Admission** (Phase 3) – patient consumption reference

## Architecture

### Layers

| Layer | Component | Responsibility |
|-------|-----------|---------------|
| Controller | `InventoryController`, `SupplierController` | REST endpoints for stock, transactions, and suppliers |
| Application Service | `InventoryApplicationService` | Inventory transaction processing, stock queries, supplier management |
| Domain Service | `InventoryDomainService` | Validates stock availability, expiration rules, non-negative quantity invariants |
| Repository | `ResourceInventoryRepository`, `InventoryTransactionRepository`, `ResourceSupplierRepository` | Data persistence and JPA specifications |
| Mapper | `InventoryMapper`, `SupplierMapper` | MapStruct entity-DTO mappings |
| Events | `DomainEventPublisher` | Publishes `InventoryUpdatedEvent`, `InventoryLowStockEvent`, `InventoryExpiredEvent` |

### Entities

**ResourceInventory**
- UUID primary key (`id`)
- `resourceId`: UUID FK
- `location`: String
- `currentStock`: Integer
- `expirationDate`: LocalDate (nullable)
- `batchNumber`: String (nullable)
- Timestamps: `createdAt`, `updatedAt`

**InventoryTransaction**
- UUID primary key (`id`)
- `resourceInventoryId`: UUID FK
- `transactionType`: String (`IN`, `OUT`, `ADJUSTMENT`, `EXPIRED_DISCARD`, `RETURN`)
- `quantity`: Integer
- `admissionId`: UUID FK (nullable)
- `referenceDocument`: String (nullable)
- `notes`: String (nullable)
- `performedBy`: UUID FK
- `transactionTimestamp`: Instant

**ResourceSupplier**
- UUID primary key (`id`)
- `name`: String
- `contactPerson`: String
- `phoneNumber`: String
- `email`: String
- `address`: String
- `leadTimeDays`: Integer
- `isActive`: Boolean

### Business Rules

1. **Non-Negative Stock**: Inventory quantity can never drop below zero.
2. **FEFO Allocation & Expiry Shielding**: Expired stock cannot be allocated or issued to admissions.
3. **Auditable Stock Movements**: Every stock change must be recorded in an immutable transaction ledger.
4. **Category Association**: Every inventory item must link to a valid resource category.

### Domain Events

- `InventoryUpdatedEvent` – published on stock adjustments, receipts, or consumption
- `InventoryLowStockEvent` – published when stock falls below reorder point / minimum threshold
- `InventoryExpiredEvent` – published when batches pass expiration date
