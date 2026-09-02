# Bed Module Plan

## [S1] Entity

**Package**: `com.hospital.resource.bed.entity`

**Table**: `beds`

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, auto-generated | |
| bed_number | String(20) | NOT NULL | Unique within ward |
| ward_id | UUID | NOT NULL, FK | References wards.id |
| bed_type | String(40) | NOT NULL | STANDARD, ICU, SURGICAL, ISOLATION, PEDIATRIC |
| is_isolation_capable | Boolean | NOT NULL, default false | |
| status | String(30) | NOT NULL, default "AVAILABLE" | AVAILABLE, OCCUPIED, CLEANING_REQUIRED, MAINTENANCE |
| current_admission_id | UUID | | Set when bed is occupied |
| last_maintenance_at | Instant | | |
| created_at | Instant | NOT NULL, auto-set | |
| updated_at | Instant | NOT NULL, auto-set | |
| created_by | UUID | | |
| updated_by | UUID | | |

**Unique constraint**: (bed_number, ward_id)

**Helper method**: `isAvailable()` → checks status == "AVAILABLE"

## [S2] Repository

**Interface**: `BedRepository extends JpaRepository<Bed, UUID>`

Custom queries:
- `findByWardIdAndStatus(UUID wardId, String status)` → List
- `countByWardIdAndStatus(UUID wardId, String status)` → long
- `countAvailableByWardId(UUID wardId)` → long (JPQL)
- `countOccupiedByWardId(UUID wardId)` → long (JPQL)
- `findByStatusAndIsIsolationCapable(String status, Boolean isIsolationCapable)` → List
- `findByWardId(UUID wardId)` → List (all beds in ward)
- `findByBedType(String bedType)` → List

## [S3] DTOs

**BedRequest**:
- bedNumber: @NotBlank
- wardId: @NotNull UUID
- bedType: @NotBlank
- isIsolationCapable: Boolean (optional)

**BedResponse**:
- All entity fields

**BedAvailabilityResponse** (availability summary):
- wardId, wardName
- totalBeds, availableBeds, reservedBeds, occupiedBeds

**BedFilterRequest** (search/filter):
- wardId: UUID (optional)
- bedType: String (optional)
- status: String (optional)
- isIsolationCapable: Boolean (optional)

## [S4] Mapper

**Interface**: `BedMapper` (MapStruct)

Methods:
- `toEntity(BedRequest)` → Bed
- `toResponse(Bed)` → BedResponse
- `toAvailabilityResponse(...)` → BedAvailabilityResponse
- `toResponseList(List<Bed>)` → List<BedResponse>

## [S5] Service

**Application Service**: `BedApplicationService`

Operations:
| Operation | Method | Transaction |
|-----------|--------|-------------|
| Create bed | createBed | @Transactional |
| Get bed by ID | getBed | readOnly |
| Get beds by ward | getBedsByWard | readOnly |
| Get available isolation beds | getAvailableIsolationBeds | readOnly |
| Get bed availability for ward | getBedAvailability | readOnly |
| Update bed | updateBed | @Transactional |
| Update bed status | updateBedStatus | @Transactional |
| Filter beds | filterBeds | readOnly |

**Domain Service**: `BedDomainService`

Responsibilities:
- Availability query logic
- Status transition validation
- Ward lookup with bed aggregation

## [S6] Controller

**Base path**: `/api/v1/beds`

| Method | Path | Description |
|--------|------|-------------|
| POST | / | Create bed |
| GET | /{id} | Get bed by ID |
| GET | /ward/{wardId} | Get beds by ward |
| GET | /available/isolation | Get available isolation beds |
| GET | /availability/{wardId} | Get bed availability summary |
| GET | /filter | Filter beds by criteria |
| PUT | /{id} | Update bed |
| PUT | /{id}/status | Update bed status |

## [S7] Availability Queries

- Get all available beds in a ward
- Get all available isolation-capable beds
- Get bed availability summary (counts by status)
- Filter beds by ward, type, status, isolation capability

## [S8] Bed Status Management

Valid status transitions:
- AVAILABLE → OCCUPIED (admission)
- AVAILABLE → MAINTENANCE
- OCCUPIED → AVAILABLE (discharge)
- OCCUPIED → CLEANING_REQUIRED
- CLEANING_REQUIRED → AVAILABLE (after cleaning)
- MAINTENANCE → AVAILABLE (after maintenance)

## [S9] Ward Lookup

- Get all beds in a specific ward
- Aggregate bed counts per ward for capacity planning
- Ward must exist before bed can be assigned

## [S10] Bed Type Filtering

- Filter by bed type (ICU, STANDARD, etc.)
- Combine with ward and status filters
- Isolation capability as separate filter

## [S11] Validation

- Bed number must be unique within ward
- Ward must exist (wardId validation)
- Status transitions must follow valid rules
- Bed type must be valid enum value

## [S12] Testing

- Repository tests: availability queries, filtering
- Service tests: CRUD, status transitions, availability
- Controller tests: endpoints, filtering, validation
