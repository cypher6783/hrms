# 07 — Recommendation Engine Implementation Guide

## 1. Scoring Pipeline Overview

```
Trigger Event
    ↓
Context Gathering
    ↓
Hard Constraint Filtering
    ↓
Weighted Scoring
    ↓
Ranking
    ↓
Confidence Calculation
    ↓
Rationale Generation
    ↓
Fallback Check
    ↓
Recommendation Persistence
    ↓
Notification
```

---

## 2. Rule Execution Order

### 2.1 Trigger Events

| Priority | Event | Recommendation Types |
|----------|-------|---------------------|
| 1 (High) | New Admission Created | Bed, Staff, Equipment, Resource |
| 2 (High) | Patient Severity Changed | Bed (re-evaluation), Staff, Equipment |
| 3 (Medium) | Bed Released (discharge/transfer) | Bed (for pending admissions) |
| 4 (Medium) | Staff Availability Changed | Staff (reassignment) |
| 5 (Low) | Equipment Maintenance Completed | Equipment (reassignment) |
| 6 (Medium) | Resource Stock Updated | Resource (for pending admissions) |
| 7 (Low) | Scheduled Refresh | All (periodic re-evaluation) |

### 2.2 Execution Sequence

1. Detect trigger event
2. Gather context from all modules
3. Apply hard constraints (eliminate invalid options)
4. Score remaining options with weighted factors
5. Rank by composite score
6. Calculate confidence scores
7. Generate human-readable rationale
8. Check fallback if no high-confidence options
9. Persist recommendation and items
10. Send notification to Ward Manager
11. Start expiry timer (30 minutes)

---

## 3. Hard Constraints

### 3.1 Bed Constraints

| Constraint | Rule | Action |
|-----------|------|--------|
| Isolation Match | If infection_status = CONFIRMED, bed must be isolation-capable | Eliminate non-isolation beds |
| Bed Status | Bed.status must be AVAILABLE | Eliminate occupied, reserved, maintenance, cleaning beds |
| Ward Capacity | Ward bed count < max_bed_capacity | Eliminate beds in full wards |
| Ward Status | Ward.status must be ACTIVE | Eliminate beds in inactive wards |

### 3.2 Staff Constraints

| Constraint | Rule | Action |
|-----------|------|--------|
| Availability | Staff.availability_status = AVAILABLE | Eliminate on-leave, off-duty staff |
| Workload | Staff workload < max_workload_threshold | Eliminate overworked staff |
| Certification | If ward is critical-care, staff certification must be VALID and not expired | Eliminate uncertified staff |

### 3.3 Equipment Constraints

| Constraint | Rule | Action |
|-----------|------|--------|
| Equipment Status | Equipment.status = AVAILABLE | Eliminate in-use, maintenance, out-of-service |
| Maintenance | No overdue maintenance | Eliminate equipment with overdue maintenance |

### 3.4 Resource Constraints

| Constraint | Rule | Action |
|-----------|------|--------|
| Stock Level | ResourceInventory.current_stock > 0 | Eliminate out-of-stock resources |
| Expiration | ResourceInventory.expiration_date > NOW() or NULL | Eliminate expired resources |

---

## 4. Weighted Scoring

### 4.1 Bed Scoring Factors

| Factor | Weight | Scoring Logic |
|--------|--------|---------------|
| Isolation Match | 0.30 | CONFIRMED + Isolation Bed = 1.0; CONFIRMED + General = 0.0; SUSPECTED + Isolation = 0.8; SUSPECTED + General = 0.6; RULED_OUT = 0.5 (any bed) |
| Bed Type Match | 0.25 | Critical → ICU = 1.0; Severe → Isolation = 0.9; Moderate → General = 0.8; Mild → General = 1.0 |
| Ward Occupancy | 0.20 | Score = 1.0 - (current_occupancy / max_capacity); lower occupancy = higher score |
| Proximity to Nursing Station | 0.10 | Closer beds score higher; configurable per ward |
| Cleaning Recency | 0.10 | Score = 1.0 - (hours_since_cleaning / 24); more recent = higher |
| Equipment Availability | 0.05 | Percentage of required equipment available in ward |

### 4.2 Staff Scoring Factors

| Factor | Weight | Scoring Logic |
|--------|--------|---------------|
| Specialization Match | 0.30 | Exact match = 1.0; Related = 0.7; General = 0.4 |
| Workload Balance | 0.25 | Score = 1.0 - (current_workload / max_threshold); lower workload = higher |
| Availability | 0.20 | AVAILABLE = 1.0; OFF_DUTY = 0.0; ON_LEAVE = 0.0 |
| Certification Status | 0.15 | VALID = 1.0; Expiring Soon (within 30 days) = 0.5; Expired = 0.0 |
| Ward Familiarity | 0.10 | Same ward = 1.0; Different ward = 0.5 |

### 4.3 Equipment Scoring Factors

| Factor | Weight | Scoring Logic |
|--------|--------|---------------|
| Equipment Type Match | 0.35 | Required type available = 1.0; Alternative type = 0.5; Not available = 0.0 |
| Equipment Status | 0.25 | AVAILABLE = 1.0; IN_USE = 0.0; UNDER_MAINTENANCE = 0.0 |
| Maintenance Recency | 0.15 | Score = 1.0 - (days_since_maintenance / 365); more recent = higher |
| Location Proximity | 0.15 | Closer to patient ward = higher score |
| Utilization History | 0.10 | Lower recent utilization = higher score (availability preference) |

### 4.4 Resource Scoring Factors

| Factor | Weight | Scoring Logic |
|--------|--------|---------------|
| Severity-Based Priority | 0.30 | CRITICAL = 1.0; SEVERE = 0.8; MODERATE = 0.5; MILD = 0.2 |
| Stock Availability | 0.25 | Score = current_stock / (minimum_threshold * 2); higher stock = higher |
| Criticality Match | 0.20 | Critical resource + Critical patient = 1.0; High + High = 0.8; etc. |
| Expiration Proximity | 0.15 | FEFO: Closer expiration = higher score (use first) |
| Historical Consumption | 0.10 | Higher consumption rate for similar patients = higher priority |

---

## 5. Composite Score Calculation

```
Composite Score = Σ (Factor Score × Factor Weight)
```

Where:
- Factor Score ∈ [0.0, 1.0]
- Factor Weight ∈ [0.0, 1.0]
- Σ Factor Weights = 1.0 for each recommendation type

### 5.1 Score Range

| Score Range | Confidence Level | Presentation |
|-------------|-----------------|--------------|
| 0.80 – 1.00 | High | Primary recommendation |
| 0.50 – 0.79 | Medium | Alternative recommendation |
| 0.30 – 0.49 | Low | Warning, suggest manual review |
| 0.00 – 0.29 | Very Low | Not presented to user |

---

## 6. Ranking

### 6.1 Ranking Rules

1. Sort options by composite score (descending)
2. Rank 1 = highest score (primary recommendation)
3. Rank 2+ = alternatives (lower scores)
4. Maximum 3 alternatives per recommendation type
5. Items below 0.30 confidence not included

### 6.2 Tie-Breaking

When two options have the same composite score:
1. Prefer option with higher individual factor score for the most important factor
2. Prefer option with lower recent utilization
3. Prefer option with more recent maintenance/cleaning

---

## 7. Confidence Calculation

```
Confidence Score = Composite Score
```

The confidence score is the composite score itself, ranging from 0.00 to 1.00.

### 7.1 Confidence Thresholds

| Threshold | Value | Action |
|-----------|-------|--------|
| High | ≥ 0.80 | Presented as primary recommendation |
| Medium | 0.50 – 0.79 | Presented as alternative |
| Low | 0.30 – 0.49 | Presented with warning |
| Minimum | < 0.30 | Not presented; fallback triggered |

---

## 8. Rationale Generation

### 8.1 Rationale Format

```
Recommendation: {EntityType} {EntityName} in {Location}
Confidence: {Score} ({Level})

Scoring Breakdown:
- {Factor1}: {Score1} × {Weight1} = {WeightedScore1} ({Explanation})
- {Factor2}: {Score2} × {Weight2} = {WeightedScore2} ({Explanation})
- ...

Alternatives:
1. {EntityType} {EntityName} (Score: {Score})
2. {EntityType} {EntityName} (Score: {Score})
```

### 8.2 Rationale Example

```
Recommendation: Bed B-12 in Ward Isolation-A
Confidence: 0.87 (High)

Scoring Breakdown:
- Isolation Match: 1.00 × 0.30 = 0.30 (Patient confirmed; bed isolation-capable)
- Bed Type Match: 0.90 × 0.25 = 0.23 (ICU bed meets Severe requirement)
- Ward Occupancy: 0.85 × 0.20 = 0.17 (Ward at 65% capacity)
- Proximity: 0.90 × 0.10 = 0.09 (Near nursing station)
- Cleaning: 0.95 × 0.10 = 0.10 (Cleaned 30 minutes ago)
- Equipment: 0.80 × 0.05 = 0.04 (Ventilator available in ward)

Alternatives:
1. Bed B-08 in Ward Isolation-B (Score: 0.79)
2. Bed B-15 in Ward Isolation-A (Score: 0.74)
```

---

## 9. Fallback Logic

### 9.1 Fallback Triggers

- No options meet minimum confidence threshold (0.30)
- All candidate options violate hard constraints
- Resource stock is critically low for all required resources
- Ward capacity is fully utilized across all wards

### 9.2 Fallback Actions

| Scenario | Fallback Strategy |
|----------|-------------------|
| No bed available | Generate "Capacity Alert" notification; recommend transfer |
| No staff available | Generate "Staffing Alert" notification; recommend on-call |
| No equipment available | Generate "Equipment Alert" notification; recommend procurement |
| No resource available | Generate "Resource Shortage" notification; recommend emergency procurement |

### 9.3 Escalation

- Escalation notification sent to Ward Manager and Administrator
- Escalation includes specific constraint that prevented recommendation
- Escalation includes current resource status summary

---

## 10. Recommendation Lifecycle

### 10.1 Generation

1. AllocationRecommendation created (status = PENDING)
2. RecommendationItems created for each resource type
3. Items have rank, confidence, rationale, scoring_breakdown

### 10.2 Action

**Accept**:
1. RecommendationDecision created (type = ACCEPTED)
2. Item status = ACCEPTED
3. Allocation executed (bed/staff/equipment/resource assigned)
4. Audit log recorded

**Override**:
1. RecommendationDecision created (type = OVERRIDDEN)
2. Item status = OVERRIDDEN
3. Alternative allocation executed
4. Audit log recorded

**Timeout**:
1. Recommendation status = EXPIRED
2. All items status = EXPIRED
3. Re-evaluation triggered
4. Escalation notification sent

---

## 11. Configurable Parameters

| Parameter | Default | Description |
|-----------|---------|-------------|
| bed.isolation.weight | 0.30 | Weight for isolation match |
| bed.type.weight | 0.25 | Weight for bed type match |
| bed.occupancy.weight | 0.20 | Weight for ward occupancy |
| bed.proximity.weight | 0.10 | Weight for proximity |
| bed.cleaning.weight | 0.10 | Weight for cleaning recency |
| bed.equipment.weight | 0.05 | Weight for equipment availability |
| staff.specialization.weight | 0.30 | Weight for specialization match |
| staff.workload.weight | 0.25 | Weight for workload balance |
| staff.availability.weight | 0.20 | Weight for availability |
| staff.certification.weight | 0.15 | Weight for certification |
| staff.ward.weight | 0.10 | Weight for ward familiarity |
| equipment.type.weight | 0.35 | Weight for type match |
| equipment.status.weight | 0.25 | Weight for status |
| equipment.maintenance.weight | 0.15 | Weight for maintenance recency |
| equipment.proximity.weight | 0.15 | Weight for location proximity |
| equipment.utilization.weight | 0.10 | Weight for utilization history |
| resource.severity.weight | 0.30 | Weight for severity priority |
| resource.stock.weight | 0.25 | Weight for stock availability |
| resource.criticality.weight | 0.20 | Weight for criticality match |
| resource.expiration.weight | 0.15 | Weight for FEFO |
| resource.consumption.weight | 0.10 | Weight for historical consumption |
| recommendation.confidence.high | 0.80 | High confidence threshold |
| recommendation.confidence.medium | 0.50 | Medium confidence threshold |
| recommendation.confidence.low | 0.30 | Low confidence threshold |
| recommendation.expiry.minutes | 30 | Recommendation expiry time |
| recommendation.fallback.enabled | true | Enable fallback recommendations |
| recommendation.escalation.minutes | 15 | Time before escalation |

---

## 12. Document References

| Document | Reference |
|----------|-----------|
| Recommendation Engine Design | `docs/planning/08-recommendation-engine-design.md` |
| CDS Engine Service Design | `docs/design/09-service-design.md` §3.1 |
| Domain Service Design | `docs/design/09-service-design.md` §3 |
| Configuration Parameters | `docs/planning/08-recommendation-engine-design.md` §10 |
