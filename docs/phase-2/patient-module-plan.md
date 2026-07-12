# Patient Module Plan

## [S1] Entity

**Package**: `com.hospital.resource.patient.entity`

**Table**: `patients`

| Field | Type | Constraints | Notes |
|-------|------|-------------|-------|
| id | UUID | PK, auto-generated | Hibernate UUID generator |
| patient_number | String(20) | UNIQUE, NOT NULL | Auto-generated via NumberGenerator |
| full_name | String(100) | NOT NULL | |
| date_of_birth | LocalDate | NOT NULL | Must be in the past |
| gender | String(10) | NOT NULL | |
| phone_number | String(20) | | |
| address | TEXT | | |
| next_of_kin_name | String(100) | | |
| next_of_kin_phone | String(20) | | |
| is_active | Boolean | NOT NULL, default true | Soft delete flag |
| created_at | Instant | NOT NULL, auto-set | Audit field |
| updated_at | Instant | NOT NULL, auto-set | Audit field |
| created_by | UUID | | Audit field |
| updated_by | UUID | | Audit field |

**Lifecycle hooks**: `@PrePersist`, `@PreUpdate` for audit timestamps.

## [S2] Repository

**Interface**: `PatientRepository extends JpaRepository<Patient, UUID>`

Custom queries:
- `findByPatientNumber(String patientNumber)` → Optional
- `existsByPatientNumber(String patientNumber)` → boolean
- `searchPatients(String search, Pageable)` → Page with JPQL search across fullName, patientNumber, phoneNumber
- `countByIsActiveTrue()` → long

## [S3] DTOs

**PatientRequest** (create/update):
- fullName: @NotBlank
- dateOfBirth: @Past
- gender: @NotBlank
- phoneNumber, address, nextOfKinName, nextOfKinPhone: optional

**PatientResponse** (all fields from entity)

**PatientSearchRequest**:
- search: String
- gender: String
- isActive: Boolean

**PatientSummaryResponse** (compact):
- id, patientNumber, fullName, isActive

## [S4] Mapper

**Interface**: `PatientMapper` (MapStruct)

Methods:
- `toEntity(PatientRequest)` → Patient
- `toResponse(Patient)` → PatientResponse
- `toSummary(Patient)` → PatientSummaryResponse
- `toResponseList(List<Patient>)` → List<PatientResponse>

## [S5] Service

**Application Service**: `PatientApplicationService`

Operations:
| Operation | Method | Transaction |
|-----------|--------|-------------|
| Create patient | createPatient | @Transactional |
| Get patient by ID | getPatient | readOnly |
| Get patient by number | getPatientByNumber | readOnly |
| Search with pagination | searchPatients | readOnly |
| Update patient | updatePatient | @Transactional |
| Soft delete (deactivate) | deactivatePatient | @Transactional |
| Count active patients | getActivePatientCount | readOnly |

**Business rules**:
- Patient number auto-generated on creation
- Soft delete via isActive flag (not physical delete)
- Search across fullName, patientNumber, phoneNumber (case-insensitive)

## [S6] Controller

**Base path**: `/api/v1/patients`

| Method | Path | Description |
|--------|------|-------------|
| POST | / | Create patient |
| GET | /{id} | Get patient by ID |
| GET | /number/{patientNumber} | Get patient by number |
| GET | / | Search with pagination |
| PUT | /{id} | Update patient |
| DELETE | /{id} | Soft delete patient |

All responses wrapped in `ApiResponse<T>`.

## [S7] Validation

- Jakarta Validation on DTOs
- Business validation in service (patient existence, uniqueness)
- GlobalExceptionHandler handles validation errors

## [S8] Search & Pagination

- Search by name, patient number, phone
- Paginated results via PageRequest
- Default page size: 20

## [S9] Soft Delete

- `isActive` flag set to false
- Inactive patients excluded from search queries
- No physical deletion

## [S10] Testing

- Repository tests: query methods, search
- Service tests: CRUD operations, validation
- Controller tests: endpoint validation, response wrapping
