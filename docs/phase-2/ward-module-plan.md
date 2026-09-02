# Ward Module Plan

## [S1] Entity

**Package**: `com.hospital.resource.ward.entity`

**Table**: `wards`

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, auto-generated | |
| name | String(50) | UNIQUE, NOT NULL | |
| ward_type | String(30) | NOT NULL | ICU, EMERGENCY, GENERAL, SURGICAL, PEDIATRIC, MATERNITY |
| max_bed_capacity | Integer | NOT NULL | Physical bed slots |
| isolation_level | String(20) | NOT NULL, default "NONE" | NONE, STANDARD, HIGH |
| equipment_zone | String(50) | | Zone for equipment allocation |
| status | String(20) | NOT NULL, default "ACTIVE" | ACTIVE, INACTIVE |
| created_at | Instant | NOT NULL, auto-set | |
| updated_at | Instant | NOT NULL, auto-set | |
| created_by | UUID | | |
| updated_by | UUID | | |

**Helper method**: `isActive()` → checks status == "ACTIVE"

## [S2] Repository

**Interface**: `WardRepository extends JpaRepository<Ward, UUID>`

Custom queries:
- `findByStatus(String status)` → List
- `findByIsActiveTrue()` → List

## [S3] DTOs

**WardRequest**:
- name: @NotBlank
- wardType: @NotBlank
- maxBedCapacity: @Min(1)
- isolationLevel: String (optional, defaults to NONE)
- equipmentZone: String (optional)

**WardResponse**:
- All entity fields

**WardStatusResponse** (capacity info):
- wardId, wardName
- totalBeds, availableBeds, occupiedBeds, cleaningBeds
- occupancyRate: Double (percentage)

## [S4] Mapper

**Interface**: `WardMapper` (MapStruct)

Methods:
- `toEntity(WardRequest)` → Ward
- `toResponse(Ward)` → WardResponse
- `toStatusResponse(Ward, long totalBeds, long availableBeds, long occupiedBeds, long cleaningBeds)` → WardStatusResponse
- `toResponseList(List<Ward>)` → List<WardResponse>

## [S5] Service

**Application Service**: `WardApplicationService`

Operations:
| Operation | Method | Transaction |
|-----------|--------|-------------|
| Create ward | createWard | @Transactional |
| Get ward by ID | getWard | readOnly |
| List active wards | getAllActiveWards | readOnly |
| Update ward | updateWard | @Transactional |
| Deactivate ward | deactivateWard | @Transactional |
| Count active wards | getActiveWardCount | readOnly |
| Get ward status with capacity | getWardStatus | readOnly |

**Domain Service**: `WardDomainService`

Responsibilities:
- Capacity calculation
- Occupancy rate calculation
- Active bed count

## [S6] Controller

**Base path**: `/api/v1/wards`

| Method | Path | Description |
|--------|------|-------------|
| POST | / | Create ward |
| GET | /{id} | Get ward by ID |
| GET | / | List all active wards |
| GET | /{id}/status | Get ward capacity status |
| PUT | /{id} | Update ward |
| DELETE | /{id} | Deactivate ward |

## [S7] Capacity & Occupancy

**Capacity calculation**:
- totalBeds: count of all beds in ward (regardless of status)
- availableBeds: count of beds with status AVAILABLE
- occupiedBeds: count of beds with status OCCUPIED
- cleaningBeds: count of beds with status CLEANING_REQUIRED

**Occupancy rate**:
- Formula: (occupiedBeds / maxBedCapacity) * 100
- Returns percentage as Double

**Active bed count**:
- All beds excluding MAINTENANCE status

## [S8] Validation

- Ward name must be unique
- maxBedCapacity must be >= 1
- Ward type must be valid enum value

## [S9] Testing

- Repository tests: status queries
- Service tests: CRUD, capacity calculation, occupancy rate
- Controller tests: endpoints, validation
