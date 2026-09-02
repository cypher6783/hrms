# Phase 2 – Core Domain Implementation Order

## Mandatory Implementation Sequence

| Phase | Module | Rationale |
|-------|--------|-----------|
| 1 | Patient | Foundation entity. No dependencies. |
| 2 | Clinical Assessment | Depends on Patient. Append-only design. |
| 3 | Ward | Independent of assessment. Structural entity. |
| 4 | Bed | Depends on Ward. References Ward by UUID. |

## Dependency Graph

```
Patient ─────┬──────────────────► Clinical Assessment
             │
Ward ────────┴──────────────────► Bed
```

## Module Completeness Criteria

Each module MUST be fully implemented and verified before proceeding:

- Entity with JPA annotations, validation, audit fields
- Repository with custom queries
- Request/Response/Summary DTOs
- MapStruct Mapper
- Application Service
- Domain Service (where required)
- REST Controller
- Validation in service layer
- Unit tests (repository, service, controller)

## Rationale for Order

1. **Patient first** — Every admission and assessment references a patient.
2. **Clinical Assessment second** — Append-only; references patient. Must be validated against patient existence.
3. **Ward third** — Structural entity. No outbound dependencies.
4. **Bed last** — References ward. Requires ward to exist for assignment.
