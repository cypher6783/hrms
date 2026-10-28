# High-Priority Fixes Plan: Hospital Resource Allocation Recommendation System

**Project:** Hospital Resource Allocation Recommendation System (Lassa Fever Unit, Benue State University Teaching Hospital - BSUTH, Makurdi)  
**Date:** October 28, 2026  
**Status:** In Progress  
**Author:** DeepMind Antigravity Pair Programming Agent

---

## 1. Executive Summary & Audit Context

An in-depth independent inspection of the Hospital Resource Allocation Recommendation System was conducted across both backend (Java 21 / Spring Boot 3.2.5 / PostgreSQL / Flyway) and frontend (React 18 / TypeScript / Vite / Tailwind CSS).

The audit verified critical discrepancies and safety hazards across:
1. **Recommendation Fallback & Double-Allocation Hazards**: The recommendation engine was found to inject fabricated entity UUIDs or select occupied/unsuitable beds when hard constraints are exhausted, while recommendation acceptance lacked real-time state revalidation and pessimistic concurrency control.
2. **Database Column Length Constraints**: The database column `allocation_recommendations.status` is defined as `VARCHAR(20)`, whereas the canonical status `NO_FEASIBLE_ALLOCATION` is 22 characters long, which triggers SQL truncation/overflow errors without schema migration.
3. **API Route Discrepancies**:
   - **Bed Cleaning**: Frontend called non-existent `/bed-cleaning/tasks` and attempted direct `PUT /beds/{id}/status?status=AVAILABLE`, circumventing mandatory Lassa decontamination verification.
   - **System Audit Log**: Frontend called `GET /audit` (resolving to `GET /api/v1/audit`), but backend only implemented `/entity/...` and `/user/...` subroutes; furthermore, clinical decisions from the recommendation engine were not piped to `audit_logs`.
   - **Resource Restocking**: Frontend called `POST /resources/{id}/inventory/add`, which had no matching handler in `ResourceController` or `InventoryController`.
4. **Scoring Model Integrity**: Scoring weights and factor definitions were verified against BSUTH clinical triage guidelines (sum = 1.00).

---

## 2. Issues Discovered, Root Causes, and Proposed Corrections

### Issue 1: Fabricated Bed Candidates & Arbitrary Fallback in Recommendation Engine

- **Files / Classes / Methods:**
  - `backend/src/main/java/com/hospital/resource/recommendation/service/RecommendationApplicationService.java` (`generateRecommendation`)
  - `backend/src/main/java/com/hospital/resource/recommendation/entity/AllocationRecommendation.java`
  - `frontend/src/pages/RecommendationCenter.tsx`
- **Root Cause:**
  When `scoredBeds.isEmpty()` (i.e., no bed passes mandatory isolation, status `AVAILABLE`, or ward capacity checks), lines 354–373 of `RecommendationApplicationService.java` fall back to `allBeds.get(0)` or generate `UUID.randomUUID()`, wrap it into a `RecommendationItem` with `itemType="BED"`, rank `1`, confidence `0.20`, and batch status `PENDING`. This causes the frontend to display an invalid bed recommendation for clinical assignment.
- **Proposed Correction:**
  1. When `scoredBeds.isEmpty()`, do **not** select any bed from `allBeds` and do **not** generate random UUIDs.
  2. Set recommendation batch status explicitly to `NO_FEASIBLE_ALLOCATION`.
  3. Do not create fabricated `RecommendationItem` records representing phantom beds. (Equipment recommendations can still be included if available, or zero items).
  4. Ensure the capacity alert notification is dispatched to clinical coordinators with explicit constraint failure reasons.
  5. The frontend `RecommendationCenter.tsx` already has a dedicated UI state for `NO_FEASIBLE_ALLOCATION`, which will now correctly activate.
- **Side Effects & Compatibility Risks:**
  The frontend expects either `items` array or `status === 'NO_FEASIBLE_ALLOCATION'`. Verifying `types/index.ts` and `RecommendationCenter.tsx` ensures no runtime crashes occur when `items` contains no bed item.

---

### Issue 2: Missing Concurrency Control and Pre-Allocation Revalidation on Decision Acceptance

- **Files / Classes / Methods:**
  - `backend/src/main/java/com/hospital/resource/recommendation/service/RecommendationApplicationService.java` (`recordDecision`)
  - `backend/src/main/java/com/hospital/resource/bed/repository/BedRepository.java`
  - `backend/src/main/java/com/hospital/resource/admission/repository/AdmissionRepository.java`
- **Root Cause:**
  In `recordDecision`, when staff accept a bed recommendation (`ACCEPT`), the service blindly sets `adm.setBedId(item.getRecommendedEntityId())` and `bed.setStatus("OCCUPIED")` without:
  1. Locking the bed record.
  2. Verifying that the bed still exists and has not been deleted or reassigned.
  3. Verifying that `bed.getStatus()` is still `AVAILABLE` and `bed.getCurrentAdmissionId() == null`.
  4. Verifying that the admission is still `ADMITTED` or `ACTIVE` and does not already hold another conflicting bed.
  5. If two users concurrently accept recommendations referencing the same bed, both succeed, causing a double-allocation race condition.
- **Proposed Correction:**
  1. Add `@Lock(LockModeType.PESSIMISTIC_WRITE)` query method `findByIdWithLock(UUID id)` in `BedRepository` to acquire row-level database locks (`SELECT FOR UPDATE`).
  2. In `recordDecision`:
     - Load bed using `bedRepository.findByIdWithLock(item.getRecommendedEntityId())` within the `@Transactional` boundary.
     - Validate that bed exists (`ResourceNotFoundException`).
     - Validate that `bed.getStatus().equalsIgnoreCase("AVAILABLE")` and `bed.getCurrentAdmissionId() == null`. If not, throw `ValidationException("Bed is no longer available for allocation. Current status: " + bed.getStatus())`.
     - Validate that admission exists, is active, and can accept allocation.
     - Verify ward is active.
     - For `EQUIPMENT` items, verify equipment exists and status is `AVAILABLE` before changing to `IN_USE`.
  3. Safely reject stale recommendations without altering database state if validation fails.
- **Side Effects & Compatibility Risks:**
  Frontend must display clear error feedback if acceptance fails due to state change. `RecommendationCenter.tsx` already renders `e.response?.data?.message` inside `feedbackMsg`.

---

### Issue 3: Schema Limitation on `allocation_recommendations.status` (`VARCHAR(20)`)

- **Files / Classes:**
  - `backend/src/main/resources/db/migration/V012__create_recommendation_tables.sql`
  - `backend/src/main/resources/db/migration/V018__expand_recommendation_status_length.sql` (new)
  - `backend/src/main/java/com/hospital/resource/recommendation/entity/AllocationRecommendation.java`
- **Root Cause:**
  The `status` column in `allocation_recommendations` is typed `VARCHAR(20)`. The status string `NO_FEASIBLE_ALLOCATION` is 22 characters long. Inserting it directly will cause PostgreSQL to throw `ERROR: value too long for type character varying(20)`.
- **Proposed Correction:**
  1. Create Flyway migration `V018__expand_recommendation_status_length.sql` executing:
     `ALTER TABLE allocation_recommendations ALTER COLUMN status TYPE VARCHAR(50);`
     `ALTER TABLE recommendation_items ALTER COLUMN status TYPE VARCHAR(50);`
     `ALTER TABLE recommendation_decisions ALTER COLUMN decision_type TYPE VARCHAR(50);`
  2. Update `@Column(nullable = false, length = 50)` in `AllocationRecommendation.java`.
- **Side Effects & Compatibility Risks:**
  Zero breaking changes; seamlessly permits longer canonical status tokens.

---

### Issue 4: Bed Cleaning Workflow Endpoint Mismatch & Infection Control Bypass

- **Files / Classes / Methods:**
  - `frontend/src/pages/BedCleaning.tsx`
  - `backend/src/main/java/com/hospital/resource/bedcleaning/controller/BedCleaningController.java`
  - `backend/src/main/java/com/hospital/resource/bedcleaning/service/BedCleaningApplicationService.java`
- **Root Cause:**
  1. Frontend was requesting `GET /bed-cleaning/tasks` and `POST /bed-cleaning/tasks/{id}/complete`. Backend implements `GET /api/v1/bed-cleaning`, `GET /api/v1/bed-cleaning/pending`, and `POST /api/v1/bed-cleaning/{id}/complete`.
  2. Frontend `handleCompleteCleaning` was directly issuing `PUT /beds/{bedId}/status?status=AVAILABLE`, completely bypassing the mandatory clinical `verifyCleaning` step required for viral decontamination in the Lassa unit.
- **Proposed Correction:**
  1. Expose `/tasks` and `/tasks/{id}/complete` endpoint aliases in `BedCleaningController.java` to support backwards compatibility with legacy API contracts, while fully supporting `/api/v1/bed-cleaning`.
  2. Refactor `BedCleaning.tsx` to support the full 4-stage decontamination lifecycle:
     - `PENDING` -> `ASSIGNED` (`POST /bed-cleaning/{id}/assign`)
     - `ASSIGNED` -> `IN_PROGRESS` (`POST /bed-cleaning/{id}/start`)
     - `IN_PROGRESS` -> `COMPLETED` (`POST /bed-cleaning/{id}/complete`)
     - `COMPLETED` -> `VERIFIED` (`POST /bed-cleaning/{id}/verify`)
  3. Remove the illegal `PUT /beds/{id}/status?status=AVAILABLE` shortcut from frontend. The backend `verifyCleaning` method in `BedCleaningApplicationService.java` is the sole authorized transition that resets bed status to `AVAILABLE`.
- **Side Effects & Compatibility Risks:**
  Enhances hospital compliance and patient safety.

---

### Issue 5: Missing General Audit Log Endpoint and Audit Recording for Decisions

- **Files / Classes / Methods:**
  - `backend/src/main/java/com/hospital/resource/audit/controller/AuditController.java`
  - `backend/src/main/java/com/hospital/resource/audit/service/AuditApplicationService.java`
  - `backend/src/main/java/com/hospital/resource/audit/dto/AuditLogResponse.java`
  - `backend/src/main/java/com/hospital/resource/recommendation/service/RecommendationApplicationService.java`
  - `frontend/src/pages/AuditLog.tsx`
- **Root Cause:**
  1. `AuditController` only provided `/entity/{type}/{id}` and `/user/{userId}` endpoints. There was no general listing endpoint `GET /api/v1/audit`.
  2. `AuditLogResponse` did not include helper fields such as `username`, `action`, `justification`, causing frontend table rendering to show blanks or fall back to mock data.
  3. When recommendation decisions (ACCEPT, REJECT, OVERRIDE) were made in `RecommendationApplicationService`, no entry was written to `audit_logs`.
- **Proposed Correction:**
  1. Add `GET /api/v1/audit` to `AuditController.java` with pagination and optional filtering (`actionType`, `entityType`).
  2. In `AuditApplicationService.java`, add `getAllAuditLogs(...)` returning `PagedResponse<AuditLogResponse>` and `recordAudit(...)` to record events.
  3. Enrich `AuditLogResponse.java` with `username`, `action`, `justification`, `previousState`, `newState` to match both frontend `AuditLog` interface and backend structure.
  4. Inject `AuditApplicationService` into `RecommendationApplicationService.java` and record decisions (`RECOMMENDATION_ACCEPT`, `RECOMMENDATION_REJECT`, `RECOMMENDATION_OVERRIDE`) transactionally.
  5. Update `AuditLog.tsx` to cleanly consume `PagedResponse` or array from `GET /audit`.
- **Side Effects & Compatibility Risks:**
  Provides full auditability and regulatory compliance.

---

### Issue 6: Resource Restocking Endpoint Mismatch

- **Files / Classes / Methods:**
  - `frontend/src/pages/Resources.tsx`
  - `backend/src/main/java/com/hospital/resource/resource/controller/ResourceController.java`
  - `backend/src/main/java/com/hospital/resource/resource/service/InventoryApplicationService.java`
- **Root Cause:**
  Frontend calls `POST /resources/{id}/inventory/add` with `{ quantity }`. Backend only had `POST /api/v1/inventory/transactions` taking `InventoryTransactionRequest` (`resourceInventoryId`, `transactionType`, etc.).
- **Proposed Correction:**
  1. Add `POST /api/v1/resources/{id}/inventory/add` in `ResourceController.java` that delegates to `inventoryService.restockResource(id, quantity, notes, userId)`.
  2. Implement `restockResource` in `InventoryApplicationService.java`: find or initialize `ResourceInventory` for the given resource, increase stock, and record an `IN` inventory transaction with audit trail.
  3. Ensure `Resources.tsx` correctly handles restock response and updates inventory table.
- **Side Effects & Compatibility Risks:**
  Resolves frontend restocking failure without disrupting existing inventory transactions.

---

### Issue 7: Verification of Multi-Factor Recommendation Scoring Weights

- **Files / Classes / Methods:**
  - `backend/src/main/java/com/hospital/resource/recommendation/service/RecommendationApplicationService.java`
- **Inspection Findings:**
  The 6 scoring factors in `RecommendationApplicationService`:
  - Factor 1: Isolation Capability Match = `0.30`
  - Factor 2: Bed Type Match = `0.25`
  - Factor 3: Ward Occupancy = `0.20`
  - Factor 4: Cleaning Recency / Hygiene = `0.10`
  - Factor 5: Location Proximity / Priority Zone = `0.10`
  - Factor 6: Ward Equipment Availability = `0.05`
  - **Sum of Weights:** $0.30 + 0.25 + 0.20 + 0.10 + 0.10 + 0.05 = 1.00$
  Factor calculations evaluate live ward occupancy (`countOccupiedByWardId / maxCapacity`), patient infection status, bed isolation capabilities, and clinical severity levels dynamically.
- **Proposed Action:**
  Preserve the rule-based weighted model. Ensure comprehensive unit test coverage for scoring edge cases and constraint boundaries.

---

## 3. Test & Verification Plan

1. **Automated Unit Tests (`RecommendationApplicationServiceTest.java`)**:
   - `generateRecommendation_whenNoEligibleBeds_returnsNoFeasibleAllocationWithoutFabricatedCandidates()`
   - `generateRecommendation_whenBedOccupied_excludesOccupiedBed()`
   - `generateRecommendation_whenWardInactive_excludesBeds()`
   - `generateRecommendation_whenCapacityExhausted_excludesBeds()`
   - `recordDecision_whenBedNoLongerAvailable_throwsValidationExceptionAndDoesNotCorruptState()`
   - `recordDecision_whenBedNonexistent_throwsNotFoundException()`
   - `recordDecision_whenAccepted_recordsAuditLog()`
2. **Automated Unit Tests (`AuditApplicationServiceTest.java` / `AuditControllerTest.java`)**:
   - Verify `GET /api/v1/audit` returns paginated audit records with usernames and justifications.
   - Verify `recordAudit` creates properly hashed/structured audit records.
3. **Automated Unit Tests (`BedCleaningApplicationServiceTest.java` / `BedCleaningControllerTest.java`)**:
   - Verify full lifecycle: PENDING -> ASSIGNED -> IN_PROGRESS -> COMPLETED -> VERIFIED.
   - Verify that bed status ONLY transitions to `AVAILABLE` upon verification.
4. **Integration & Concurrency Tests**:
   - Verify pessimistic locking on bed allocation prevents race conditions under simulated concurrent access.
5. **Frontend Build & Lint**:
   - Run `npm run lint` and `npm run build` in `frontend` directory.
6. **Backend Maven Build & Test Suite**:
   - Run `mvn test` in `backend` directory.

---

## 4. Expected Completion Criteria

- [x] Inspection complete and all defects confirmed.
- [ ] Flyway migration `V018` created and verified.
- [ ] Recommendation service fallback and decision revalidation implemented and tested.
- [ ] Bed cleaning workflow aligned and direct bypass removed.
- [ ] Audit log endpoints and decision logging implemented and tested.
- [ ] Resource restocking aligned and tested.
- [ ] All automated tests pass cleanly (`mvn test`).
- [ ] Frontend compiles without errors (`npm run build`).
- [ ] Git commits cleanly pushed to `main` and `master`.
- [ ] Structured final report prepared.
