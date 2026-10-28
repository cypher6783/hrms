# Operational Resource Workflows – Phase 4

## 1. Resource Allocation & Reservation Workflow

```
[Request Reservation] ──> Validate Unreserved Stock ──> Save ResourceReservation ──> Publish ResourceReservedEvent
                                                                │
                                                                ▼
[Allocate Resource] ────> Check Active Reservation ────> Save ResourceAllocation  ──> Deduct Stock / Publish ResourceAllocatedEvent
                                                                │
                                                                ▼
[Release Resource] ─────> Mark Allocation Released ───> Return Available Stock    ──> Publish ResourceReleasedEvent
```

### Steps:
1. **Reservation**: User reserves `quantity` of a resource for an admission or department. Unreserved stock is checked; if sufficient, reservation record is stored with status `RESERVED` and expiration timestamp.
2. **Allocation**: When needed, reserved or available stock is allocated. A `ResourceAllocation` record is created, stock is updated via an `OUT` transaction, and `ResourceAllocatedEvent` is emitted.
3. **Release**: When care ends or allocation expires, `releasedAt` timestamp is recorded, stock is returned via a `RETURN` transaction, and `ResourceReleasedEvent` is emitted.

---

## 2. Inventory Transaction & Expiry Shielding Workflow

```
[Receive Stock (IN)] ────> Create InventoryBatch ─────> Add Stock Ledger Record ─> Publish InventoryUpdatedEvent
                                                                │
                                                                ▼
[Low-Stock Check] ──────> Current < MinimumThreshold ─> Trigger Low Stock Alert  ─> Publish InventoryLowStockEvent
                                                                │
                                                                ▼
[Expiry Audit] ─────────> ExpirationDate <= Today ───> Discard Expired Batch     ─> Publish InventoryExpiredEvent
```

### Steps:
1. **Stock Entry**: New stock received with location, batch number, and expiration date. Recorded in `ResourceInventory` and `InventoryTransaction`.
2. **Low-Stock Audit**: When total stock falls below `minimumThreshold` or `reorderPoint`, `InventoryLowStockEvent` is published to notify procurement.
3. **Expiry Shielding**: Queries filtering stock for allocation automatically exclude batches where `expirationDate <= current_date`. Expired batches are written off via `EXPIRED_DISCARD` transactions.

---

## 3. Equipment Lifecycle & Assignment Workflow

```
[Register Asset] ────────> Set Status: AVAILABLE ─────> Assign to Ward/Admission ─> Publish EquipmentAssignedEvent
                                                                │
                                                                ▼
                                                        Status: IN_USE
                                                                │
                                                                ▼
[Return Equipment] ──────> Clear Admission Ref ───────> Set AVAILABLE / DECONTAM  ─> Publish EquipmentReleasedEvent
```

### Steps:
1. **Registration**: Device created with unique serial number, location, and status `AVAILABLE`.
2. **Assignment**: Device assigned to active admission. Status updates to `IN_USE`, allocation record created in `equipment_allocations`, and `EquipmentAssignedEvent` is published.
3. **Return**: Device released from admission. `releasedAt` set in allocation record, status updated to `AVAILABLE` (or decontamination if applicable), and `EquipmentReleasedEvent` is published.

---

## 4. Equipment Maintenance & Return-to-Service Workflow

```
[Schedule Maintenance] ──> Status: UNDER_MAINTENANCE ──> Publish EquipmentMaintenanceStartedEvent
                                    │
                                    ▼
[Technician Work] ───────> Record Work & Notes ───────> Status: COMPLETED ──> Publish MaintenanceCompletedEvent
                                    │
                                    ▼
[Return-to-Service] ─────> Verified by Supervisor ───> Status: AVAILABLE  ──> Publish ReturnedToServiceEvent
```

### Steps:
1. **Initiation**: Maintenance scheduled or requested. Target equipment status switches to `UNDER_MAINTENANCE`, preventing assignment. `EquipmentMaintenanceStartedEvent` published.
2. **Work Completion**: Technician logs completed date, notes, and costs. Maintenance status changes to `COMPLETED`. `EquipmentMaintenanceCompletedEvent` published.
3. **Return-to-Service Verification**: Supervisor verifies equipment safety/calibration. Maintenance status changes to `VERIFIED` and equipment status returns to `AVAILABLE`. `EquipmentReturnedToServiceEvent` published.
