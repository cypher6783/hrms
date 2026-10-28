# High-Priority Fixes Checklist: Hospital Resource Allocation Recommendation System

**Project:** Hospital Resource Allocation Recommendation System  
**Unit:** Lassa Fever Isolation Unit, BSUTH Makurdi  
**Tracking Status:** Completed & Fully Verified  

---

## 1. Repository & Codebase Inspection
- [x] Inspect `RecommendationApplicationService.java` for fallback behavior and random UUID generation.
- [x] Inspect `RecommendationApplicationService.java` `recordDecision` for bed existence, status validation, and concurrency safeguards.
- [x] Inspect database migrations (`V012__create_recommendation_tables.sql`) and verify status column lengths.
- [x] Inspect `BedCleaningController.java` and `BedCleaning.tsx` for route mismatches and workflow shortcuts.
- [x] Inspect `AuditController.java`, `AuditApplicationService.java`, and `AuditLog.tsx` for endpoint alignment.
- [x] Inspect `ResourceController.java`, `InventoryController.java`, and `Resources.tsx` for inventory restock alignment.
- [x] Inspect recommendation scoring weights and factor calculations ($0.30 + 0.25 + 0.20 + 0.10 + 0.10 + 0.05 = 1.00$).

---

## 2. Database & Schema Updates
- [x] Create Flyway migration `V018__expand_recommendation_status_length.sql` expanding `status` to `VARCHAR(50)`.
- [x] Update JPA entity annotations in `AllocationRecommendation.java` (`length = 50`), `RecommendationItem.java` (`length = 50`), and `RecommendationDecision.java` (`length = 50`).
- [x] Run migration against local PostgreSQL database and confirm clean startup.

---

## 3. Backend Corrections: Recommendation Engine & Allocation Safety
- [x] Remove arbitrary bed selection (`allBeds.get(0)`) and random UUID generation from `RecommendationApplicationService.java`.
- [x] Set recommendation batch status explicitly to `NO_FEASIBLE_ALLOCATION` when no beds meet hard constraints.
- [x] Suppress phantom bed items in recommendation output while preserving alert notification.
- [x] Add `@Lock(LockModeType.PESSIMISTIC_WRITE)` query method in `BedRepository.java` (`findByIdWithLock`) and `EquipmentRepository.java`.
- [x] Implement strict revalidation in `recordDecision`:
  - [x] Verify bed exists and lock row.
  - [x] Verify bed status is `AVAILABLE` and `currentAdmissionId == null`.
  - [x] Verify admission exists, is active, and can accept allocation.
  - [x] Verify ward is active and capacity not exceeded.
  - [x] Throw `ValidationException` on state violations and prevent dirty database writes.
  - [x] Validate equipment availability for `EQUIPMENT` items.
  - [x] Mandate non-empty justifications on `OVERRIDE` decisions.

---

## 4. Backend Corrections: Audit Trail & Governance
- [x] Add `GET /api/v1/audit` to `AuditController.java` supporting pagination and action/entity filtering.
- [x] Implement `getAllAuditLogs` in `AuditApplicationService.java`.
- [x] Add `recordAudit` helper in `AuditApplicationService.java` to record structured audit entries with SHA-256 integrity hashes.
- [x] Enrich `AuditLogResponse.java` with `username`, `action`, `justification`, `previousState`, `newState`.
- [x] Inject `AuditApplicationService` into `RecommendationApplicationService.java` to transactionally record all clinical decisions (`ACCEPT`, `REJECT`, `OVERRIDE`).

---

## 5. Backend Corrections: Bed Cleaning & Inventory Alignment
- [x] Add backwards-compatible alias routes in `BedCleaningController.java` for `/tasks` and `/tasks/{id}/complete`.
- [x] Add restock endpoint in `ResourceController.java` (`POST /api/v1/resources/{id}/inventory/add`).
- [x] Implement `restockResource` in `InventoryApplicationService.java` with stock increment and `IN` transaction logging.

---

## 6. Frontend & API Client Alignment
- [x] Update `frontend/src/pages/BedCleaning.tsx`:
  - [x] Align with `/api/v1/bed-cleaning` endpoints.
  - [x] Support full 4-stage decontamination lifecycle: Assign, Start, Complete, Verify.
  - [x] Remove direct `PUT /beds/{bedId}/status?status=AVAILABLE` shortcut so bed availability is strictly gated behind verification.
- [x] Update `frontend/src/pages/AuditLog.tsx`:
  - [x] Connect directly to `GET /audit` (resolves to `/api/v1/audit`) using paginated/array response.
  - [x] Ensure username, action badge, entity, and clinical justification render correctly.
- [x] Update `frontend/src/pages/Resources.tsx`:
  - [x] Verify restock modal interacts cleanly with restock API.
- [x] Verify `frontend/src/pages/RecommendationCenter.tsx`:
  - [x] Ensure `NO_FEASIBLE_ALLOCATION` banner renders with clinical guidance when hard constraints fail.

---

## 7. Automated Tests & Build Verification
- [x] Update and expand `RecommendationApplicationServiceTest.java`:
  - [x] Test eligible bed recommended correctly.
  - [x] Test occupied bed excluded.
  - [x] Test inactive ward excluded.
  - [x] Test isolation-incompatible bed excluded for confirmed patient.
  - [x] Test empty/exhausted candidates returns `NO_FEASIBLE_ALLOCATION` without fabricated UUIDs.
  - [x] Test accepting recommendation for unavailable bed throws `ValidationException` safely.
  - [x] Test decision acceptance records audit log entry.
- [x] Add or update `AuditControllerTest.java` / `AuditApplicationServiceTest.java`.
- [x] Add or update `BedCleaningApplicationServiceTest.java`.
- [x] Run backend test suite: `cd backend; mvn test` (232 tests passing, 0 failures, 0 errors).
- [x] Run frontend build and typecheck: `cd frontend; npm run build` (compiled clean in 2.23s, 0 errors).

---

## 8. Final Delivery & Reporting
- [x] Synchronize git branches (`main` and `master`) and push to remote.
- [x] Produce the comprehensive Section 10 Final Report detailing findings, fixes, test output, and Chapter 4 readiness classification.
