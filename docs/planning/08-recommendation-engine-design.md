# 08 — Rule-Based Clinical Decision Support Engine Design

## 1. Goals

The Rule-Based Clinical Decision Support (CDS) Engine is the intelligence layer of the system. Its purpose is to transform raw operational data into actionable allocation recommendations that optimize resource utilization while maintaining patient safety and compliance with infection control protocols.

This engine uses deterministic rules and weighted scoring algorithms rather than machine learning. The design is suitable for an undergraduate project and provides predictable, auditable, and explainable recommendations.

### 1.1 Primary Goals

1. **Optimize Bed Allocation**: Assign patients to the most appropriate available bed based on severity, isolation requirements, and ward capacity.
2. **Balance Staff Workload**: Distribute patient assignments across available staff based on specialization, workload, and availability.
3. **Prioritize Equipment Assignment**: Allocate equipment to patients with the greatest clinical need while respecting maintenance schedules.
4. **Manage Resource Consumption**: Recommend consumable allocations based on patient severity, stock levels, and historical consumption patterns.
5. **Provide Transparency**: Present recommendations with clear rationale and confidence scores to support informed decision-making.

### 1.2 Design Principles

- **Rule-Based**: The engine uses deterministic rules and weighted scoring, ensuring predictable and auditable behavior. No machine learning in the initial release.
- **Multi-Factor**: Recommendations consider all relevant factors simultaneously, not just a single criterion.
- **Explainable**: Every recommendation includes a human-readable rationale explaining the scoring.
- **Overridable**: Clinical judgment always takes precedence; the engine recommends, humans decide.
- **Iterative**: The engine produces ranked alternatives, not just a single recommendation.

---

## 2. Recommendation Workflow

### 2.1 Trigger Events

The engine generates recommendations in response to the following events:

| Event | Recommendation Type | Priority |
|-------|---------------------|----------|
| New Admission Created | Bed, Staff, Equipment, Resource | High |
| Patient Severity Changed | Bed (re-evaluation), Staff, Equipment | High |
| Bed Released (discharge/transfer) | Bed (for pending admissions) | Medium |
| Staff Availability Changed | Staff (reassignment) | Medium |
| Equipment Maintenance Completed | Equipment (reassignment) | Low |
| Resource Stock Updated | Resource (for pending admissions) | Medium |
| Scheduled Refresh | All (periodic re-evaluation) | Low |

### 2.2 Workflow Steps

```
┌─────────────────────────────────────────────────────────┐
│  1. TRIGGER EVENT DETECTED                               │
│     (New admission, severity change, resource update)    │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  2. CONTEXT GATHERING                                    │
│     Query current state from all resource modules:      │
│     - Latest ClinicalAssessment (severity, triage,      │
│       infection status) for the admission               │
│     - Available beds by type and isolation capability    │
│     - Ward occupancy and configuration                   │
│     - Equipment availability by type and status          │
│     - Staff workload, availability, specialization       │
│     - Resource inventory levels and transaction history  │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  3. CONSTRAINT FILTERING                                 │
│     Eliminate options that violate hard constraints:     │
│     - Confirmed patients → isolation beds only           │
│     - Full wards → no bed assignment                     │
│     - Overworked staff → no new assignments              │
│     - Out-of-service equipment → no assignment           │
│     - Empty resource stock → no allocation               │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  4. SCORING                                              │
│     Score each remaining option against weighted factors │
│     Apply factor weights per recommendation type         │
│     Calculate composite score (0.00 - 1.00)              │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  5. RANKING                                              │
│     Sort options by composite score (descending)         │
│     Select top-N recommendations per type                │
│     Generate rationale for each recommendation           │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  6. FALLBACK CHECK                                       │
│     If no options meet minimum confidence threshold:     │
│     - Generate fallback recommendations                 │
│     - Flag resource shortage or capacity issue          │
│     - Trigger escalation notification                   │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  7. DELIVERY                                             │
│     Present recommendations to user via UI              │
│     Store recommendation records in database            │
│     Log recommendation event in audit trail             │
│     Start expiry timer for pending recommendations      │
└──────────────────────┬──────────────────────────────────┘
                       │
                       ▼
┌─────────────────────────────────────────────────────────┐
│  8. ACTION                                               │
│     User accepts → Execute allocation, update status     │
│     User overrides → Record justification, execute alt  │
│     Timeout → Expire recommendation, re-evaluate        │
└─────────────────────────────────────────────────────────┘
```

---

## 3. Scoring Factors

### 3.1 Bed Recommendation Factors

| Factor | Weight | Description | Scoring Logic |
|--------|--------|-------------|---------------|
| Isolation Match | 0.30 | Whether bed isolation capability matches patient infection status | Confirmed + Isolation Bed = 1.0; Confirmed + General Bed = 0.0; Suspected + Isolation = 0.8; Suspected + General = 0.6 |
| Bed Type Match | 0.25 | Whether bed type matches severity requirement | Critical → ICU = 1.0; Severe → Isolation = 0.9; Moderate → General = 0.8 |
| Ward Occupancy | 0.20 | Current occupancy level of the ward | Lower occupancy scores higher (inverse relationship) |
| Proximity to Nursing Station | 0.10 | Physical proximity to nursing station (ward-specific) | Closer beds score higher for critical patients |
| Cleaning Recency | 0.10 | How recently the bed cleaning was completed and verified | More recently cleaned scores higher |
| Equipment Availability | 0.05 | Availability of required equipment in the ward | Higher availability scores higher |

### 3.2 Staff Recommendation Factors

| Factor | Weight | Description | Scoring Logic |
|--------|--------|-------------|---------------|
| Specialization Match | 0.30 | Whether staff specialization matches patient needs | Exact match = 1.0; Related = 0.7; General = 0.4 |
| Workload Balance | 0.25 | Current workload relative to maximum threshold | Lower workload ratio scores higher |
| Availability | 0.20 | Current availability status | Available = 1.0; Off-Duty = 0.0; On-Leave = 0.0 |
| Certification Status | 0.15 | Whether certification is current | Valid = 1.0; Expiring Soon = 0.5; Expired = 0.0 |
| Ward Familiarity | 0.10 | Whether staff is assigned to the relevant ward | Same ward = 1.0; Different ward = 0.5 |

### 3.3 Equipment Recommendation Factors

| Factor | Weight | Description | Scoring Logic |
|--------|--------|-------------|---------------|
| Equipment Type Match | 0.35 | Whether equipment type matches patient clinical needs | Required type available = 1.0; Alternative type = 0.5; Not available = 0.0 |
| Equipment Status | 0.25 | Current status of the equipment | Available = 1.0; In-Use = 0.0; Under-Maintenance = 0.0 |
| Maintenance Recency | 0.15 | How recently the equipment was maintained | More recently maintained scores higher |
| Location Proximity | 0.15 | Distance from equipment location to patient ward | Closer scores higher |
| Utilization History | 0.10 | Historical utilization rate | Lower utilization scores higher (availability preference) |

### 3.4 Resource Recommendation Factors

| Factor | Weight | Description | Scoring Logic |
|--------|--------|-------------|---------------|
| Severity-Based Priority | 0.30 | Patient severity drives allocation priority | Critical = 1.0; Severe = 0.8; Moderate = 0.5; Mild = 0.2 |
| Stock Availability | 0.25 | Current stock level relative to minimum threshold | Higher stock ratio scores higher |
| Criticality Match | 0.20 | Resource criticality level vs. patient severity | Critical resource + Critical patient = 1.0 |
| Expiration Proximity | 0.15 | Expiration date proximity (FEFO principle) | Closer expiration scores higher for allocation |
| Historical Consumption | 0.10 | Historical consumption rate for similar patients | Higher consumption rate increases allocation priority |

---

## 4. Decision Logic

### 4.1 Hard Constraints (Elimination Rules)

These constraints eliminate options entirely; no scoring is applied:

| Constraint | Module | Rule |
|-----------|--------|------|
| Isolation Requirement | Bed | Confirmed Lassa fever patients MUST be placed in isolation-capable beds. |
| Bed Occupancy | Bed | A bed occupied by a patient cannot be recommended for another patient. |
| Ward Capacity | Ward | Wards at maximum capacity cannot receive new bed assignments. |
| Staff Certification | Staff | Staff with expired certifications cannot be assigned to critical-care wards. |
| Staff Workload | Staff | Staff at or above maximum workload threshold cannot receive new assignments. |
| Equipment Status | Equipment | Equipment with status "Out-of-Service" or "Under-Maintenance" cannot be recommended. |
| Resource Stock | Resource | Resources with zero stock cannot be allocated. |
| Bed Maintenance | Bed | Beds with status "Under-Maintenance" cannot be recommended. |

### 4.2 Soft Constraints (Scoring Modifiers)

These constraints modify scores without eliminating options:

| Constraint | Effect |
|-----------|--------|
| Bed Cleaning Recency | Beds with completed and verified cleaning within the last 2 hours receive a score bonus. |
| Staff Shift Preference | Staff whose current shift matches the time period receive a score bonus. |
| Equipment Maintenance Status | Equipment nearing maintenance deadline receives a score penalty. |
| Resource Expiration | Resources nearing expiration receive a score bonus (FEFO principle). |

### 4.3 Composite Score Calculation

For each candidate option:

```
Composite Score = Σ (Factor Score × Factor Weight)
```

Where:
- Factor Score ∈ [0.0, 1.0]
- Factor Weight ∈ [0.0, 1.0]
- Σ Factor Weights = 1.0

### 4.4 Confidence Threshold

| Confidence Level | Score Range | Action |
|-----------------|-------------|--------|
| High | 0.80 - 1.00 | Present as primary recommendation |
| Medium | 0.50 - 0.79 | Present as alternative recommendation |
| Low | 0.30 - 0.49 | Present with warning; suggest manual review |
| Very Low | 0.00 - 0.29 | Not presented; trigger fallback logic |

---

## 5. Inputs

| Input | Source | Usage |
|-------|--------|-------|
| Latest ClinicalAssessment severity | ClinicalAssessment (via Admission) | Determines priority and resource requirements |
| Latest ClinicalAssessment triage | ClinicalAssessment (via Admission) | Determines urgency of recommendation |
| Latest ClinicalAssessment infection status | ClinicalAssessment (via Admission) | Determines isolation requirement |
| Bed availability and type | Bed Management | Candidate bed selection |
| Bed isolation capability | Bed Management | Isolation constraint enforcement |
| Ward occupancy rates | Ward Management | Capacity-based scoring |
| Ward isolation configuration | Ward Management | Ward suitability assessment |
| Equipment availability and status | Equipment Management | Candidate equipment selection |
| Staff workload scores | Workload Calculator | Workload balance scoring |
| Staff availability status | Staff Management | Availability constraint enforcement |
| Staff specializations | Staff Management | Specialization match scoring |
| Resource inventory levels | Resource Inventory | Stock availability scoring |
| Resource criticality | Resource Management | Priority-based allocation |
| Historical utilization patterns | Audit Logs | Trend-based scoring adjustments |
| Historical consumption patterns | Inventory Transactions | Resource consumption forecasting |
| Engine parameters | System Configuration | Configurable weights and thresholds |

---

## 6. Outputs

### 6.1 Recommendation Record

Each recommendation includes:

| Field | Description |
|-------|-------------|
| Recommendation Type | Bed, Staff, Equipment, Resource, or Composite |
| Recommended Entity | The specific bed, staff member, equipment, or resource |
| Confidence Score | Composite score (0.00 - 1.00) |
| Rationale | Human-readable explanation of scoring factors |
| Alternative Options | Ranked list of alternative recommendations |
| Expiry Timestamp | Time after which the recommendation expires |
| Linked Admission | The admission record this recommendation is for |

### 6.2 Composite Recommendation

For a new admission, the engine produces a composite recommendation containing:

1. **Primary Bed Recommendation**: Best-fit bed with rationale.
2. **Staff Assignment Recommendations**: Top-3 staff members ranked by score.
3. **Equipment Recommendations**: Required equipment ranked by availability.
4. **Resource Allocation Recommendations**: Consumable resources ranked by priority and stock.

### 6.3 Rationale Format

The rationale is a structured text that explains:

```
Recommendation: Bed B-12 in Ward Isolation-A
Confidence: 0.87 (High)

Scoring Breakdown:
- Isolation Match: 1.00 × 0.30 = 0.30 (Patient confirmed; bed isolation-capable)
- Bed Type Match: 0.90 × 0.25 = 0.23 (ICU bed meets Severe requirement)
- Ward Occupancy: 0.85 × 0.20 = 0.17 (Ward at 65% capacity)
- Proximity: 0.90 × 0.10 = 0.09 (Near nursing station)
- Sanitation: 0.95 × 0.10 = 0.10 (Sanitized 30 minutes ago)
- Equipment: 0.80 × 0.05 = 0.04 (Ventilator available in ward)

Alternatives:
1. Bed B-08 in Ward Isolation-B (Score: 0.79)
2. Bed B-15 in Ward Isolation-A (Score: 0.74)
```

---

## 7. Override Process

### 7.1 Override Workflow

1. User reviews recommendation on the interface.
2. User selects "Override" and chooses an alternative option or enters a manual selection.
3. System prompts for mandatory justification text.
4. User provides justification and confirms override.
5. System records the override with:
   - Original recommendation details
   - User-selected alternative
   - Justification text
   - Timestamp and user ID
6. System executes the user's selected allocation.
7. Override event is logged in audit trail.

### 7.2 Override Constraints

- Only users with Ward Manager, Medical Doctor, or Administrator roles may override recommendations.
- Override justification is mandatory and cannot be empty.
- Overrides are tracked for engine performance analysis.
- Repeated overrides of the same type may indicate engine tuning is needed.

### 7.3 Override Analytics

The system tracks:

| Metric | Description |
|--------|-------------|
| Override Rate | Percentage of recommendations overridden per type |
| Override Reasons | Categorized reasons for overrides |
| Override by User | Override frequency per user |
| Override by Type | Override frequency per recommendation type |
| Override Impact | Patient outcome comparison (accepted vs. overridden) |

---

## 8. Fallback Logic

### 8.1 Fallback Triggers

Fallback logic activates when:

1. No options meet the minimum confidence threshold (0.30).
2. All candidate options violate hard constraints.
3. Resource stock is critically low for all required resources.
4. Ward capacity is fully utilized across all wards.

### 8.2 Fallback Strategies

| Scenario | Fallback Strategy |
|----------|-------------------|
| No bed available | Generate "Capacity Alert" notification; recommend queuing or transfer to another unit. |
| No staff available | Generate "Staffing Alert" notification; recommend on-call activation or workload redistribution. |
| No equipment available | Generate "Equipment Alert" notification; recommend maintenance prioritization or external procurement. |
| No resource available | Generate "Resource Shortage" notification; recommend emergency procurement or allocation from reserves. |

### 8.3 Escalation

When fallback logic activates:

1. An escalation notification is sent to the Ward Manager and Administrator.
2. The escalation includes the specific constraint that prevented normal recommendation.
3. The escalation includes current resource status summary.
4. The escalation is logged in audit trail.

---

## 9. Forecasting Model

The forecasting module uses statistical models suitable for an undergraduate project. Two models are implemented:

### 9.1 Simple Moving Average (SMA)

**Use Case**: Short-term demand forecasting (7-day horizon).

**Formula**:
```
Forecast(t+1) = (1/n) × Σ Actual(t-i) for i = 0 to n-1
```

Where:
- `n` = number of periods (default: 7 for 7-day forecast)
- `Actual(t-i)` = actual admission count or resource consumption for period `t-i`

**Example**: To forecast tomorrow's admissions, average the last 7 days of admission counts.

**Implementation**:
```java
public double calculateSMA(List<Double> historicalValues, int period) {
    if (historicalValues.size() < period) {
        return historicalValues.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
    return historicalValues.subList(historicalValues.size() - period, historicalValues.size())
            .stream().mapToDouble(Double::doubleValue).average().orElse(0);
}
```

### 9.2 Weighted Moving Average (WMA)

**Use Case**: Medium-term forecasting (14-day, 30-day horizon) where recent data is more significant.

**Formula**:
```
Forecast(t+1) = Σ (Weight(i) × Actual(t-i)) / Σ Weight(i)
```

Where:
- Weights decrease linearly: Weight(0) = n, Weight(1) = n-1, ..., Weight(n-1) = 1
- Recent periods have higher weights

**Example**: For a 7-day WMA:
```
Forecast = (7×Day7 + 6×Day6 + 5×Day5 + 4×Day4 + 3×Day3 + 2×Day2 + 1×Day1) / 28
```

**Implementation**:
```java
public double calculateWMA(List<Double> historicalValues, int period) {
    double weightedSum = 0;
    double weightSum = 0;
    int n = Math.min(period, historicalValues.size());
    for (int i = 0; i < n; i++) {
        double weight = n - i;
        weightedSum += weight * historicalValues.get(historicalValues.size() - 1 - i);
        weightSum += weight;
    }
    return weightSum > 0 ? weightedSum / weightSum : 0;
}
```

### 9.3 Model Selection

| Forecast Horizon | Model | Rationale |
|-----------------|-------|-----------|
| 7-day | Simple Moving Average | Captures recent trend; sufficient for short-term. |
| 14-day | Weighted Moving Average | Gives more weight to recent data; handles trend shifts. |
| 30-day | Weighted Moving Average | Balances long-term trend with recent changes. |

### 9.4 Accuracy Measurement

Forecast accuracy is measured using Mean Absolute Percentage Error (MAPE):

```
MAPE = (1/n) × Σ |Actual - Forecast| / Actual × 100%
```

Accuracy is tracked in `forecast_snapshots` and displayed on the forecasting dashboard.

### 9.5 Future AI Integration (Phase 2)

When sufficient historical data is accumulated (12+ months, 1000+ admissions):

- Linear regression for trend analysis.
- Seasonal decomposition for Lassa fever outbreak cycles.
- Machine learning models (Random Forest, Gradient Boosting) for complex pattern recognition.
- Reinforcement learning for dynamic weight optimization based on override patterns.

---

## 10. Configuration Parameters

The following parameters are configurable by administrators:

| Parameter | Default | Description |
|-----------|---------|-------------|
| bed.isolation.weight | 0.30 | Weight for isolation match in bed scoring |
| bed.type.weight | 0.25 | Weight for bed type match |
| bed.occupancy.weight | 0.20 | Weight for ward occupancy |
| bed.proximity.weight | 0.10 | Weight for proximity to nursing station |
| bed.sanitation.weight | 0.10 | Weight for sanitation recency |
| bed.equipment.weight | 0.05 | Weight for equipment availability |
| staff.specialization.weight | 0.30 | Weight for specialization match |
| staff.workload.weight | 0.25 | Weight for workload balance |
| staff.availability.weight | 0.20 | Weight for availability status |
| staff.certification.weight | 0.15 | Weight for certification status |
| staff.ward.weight | 0.10 | Weight for ward familiarity |
| recommendation.confidence.high | 0.80 | Minimum score for high confidence |
| recommendation.confidence.medium | 0.50 | Minimum score for medium confidence |
| recommendation.confidence.low | 0.30 | Minimum score for low confidence |
| recommendation.expiry.minutes | 30 | Time before recommendation expires |
| recommendation.fallback.enabled | true | Enable fallback recommendations |
| recommendation.escalation.minutes | 15 | Time before escalation notification |

---

## 11. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Domain Model | `docs/planning/06-domain-model.md` |
| Database Plan | `docs/planning/07-database-plan.md` |
