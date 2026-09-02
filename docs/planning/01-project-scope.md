# 01 — Project Scope

## 1. Background

Lassa fever is a viral hemorrhagic fever endemic in West Africa, including Nigeria. Benue State University Teaching Hospital (BSUTH) operates a dedicated Lassa Fever Unit that manages patients presenting with suspected or confirmed Lassa fever. The unit faces significant challenges in resource allocation due to the unpredictable nature of outbreak surges, limited bed capacity, specialized equipment requirements, and the critical need for isolation compliance.

Currently, decisions regarding bed assignment, staff scheduling, equipment allocation, and consumable resource distribution are made manually by ward managers and nursing officers. This manual process introduces delays, increases the risk of human error, and may result in suboptimal resource utilization during peak periods. During outbreak surges, these inefficiencies can directly impact patient outcomes.

The Smart Hospital Resource Allocation system aims to address these challenges by providing a rule-based clinical decision support platform that recommends optimal resource allocation decisions based on real-time data, historical patterns, and defined clinical and operational rules.

## 2. Problem Statement

BSUTH's Lassa Fever Unit currently lacks an integrated digital system to manage and optimize the allocation of hospital resources. This results in:

- **Delayed resource allocation**: Manual processes slow down bed assignment and equipment provisioning during critical periods.
- **Suboptimal utilization**: Without visibility into real-time occupancy and resource status, available capacity is often underutilized or misallocated.
- **Inconsistent triage-to-resource mapping**: Different staff members may assign resources differently for comparable patient profiles.
- **No forecasting capability**: The unit cannot proactively prepare for anticipated surges based on historical data.
- **Inadequate audit trail**: Manual tracking makes it difficult to reconstruct allocation decisions for quality improvement or regulatory review.
- **Compliance risk**: Infection control protocols (isolation requirements, equipment sterilization tracking) may be inconsistently enforced without systematic enforcement.

## 3. Objectives

### 3.1 Primary Objectives

1. Develop a rule-based clinical decision support engine that suggests optimal bed, staff, equipment, and consumable allocations based on patient severity, triage level, and real-time resource availability.
2. Provide real-time visibility into bed occupancy, ward status, staff workload, and equipment availability through a centralized dashboard.
3. Implement a structured patient admission workflow from registration through triage, assessment, ward assignment, and discharge.
4. Enable forecasting of resource demand based on historical utilization patterns and seasonal trends.
5. Establish a complete audit trail for all resource allocation decisions and system interactions.
6. Enforce role-based access control to ensure appropriate system access for different user categories.

### 3.2 Secondary Objectives

1. Reduce average time-to-bed-assignment for incoming Lassa fever patients.
2. Improve resource utilization rates across the unit.
3. Support compliance with Nigerian Federal Ministry of Health infection control guidelines for Lassa fever management.
4. Provide reporting capabilities for hospital administration and public health authorities.
5. Create a foundation for future integration with hospital-wide information systems.

## 4. Scope

### 4.1 In Scope

| Area | Description |
|------|-------------|
| Patient Management | Registration, profile management (demographics only) |
| Clinical Assessment | Encounter-specific severity assessment, triage classification, infection status tracking |
| Admission Management | Admission workflow, ward assignment, transfer, discharge processing |
| Bed Management | Bed inventory, status tracking, occupancy monitoring, bed recommendation |
| Bed Cleaning Workflow | Cleaning task management, assignment, verification, status tracking |
| Ward Management | Ward configuration, capacity definition, isolation designation, status monitoring |
| Resource Management | Resource definitions, inventory tracking, allocation, reorder alerts |
| Inventory Transactions | Transactional ledger for all stock movements (purchase, issue, return, adjustment) |
| Equipment Management | Equipment registry, availability tracking, maintenance scheduling, assignment |
| Equipment Maintenance | Maintenance history, scheduling, completion tracking |
| Staff Management | Staff profiles, role assignment, workload tracking |
| Shift Management | Shift definitions, staff assignments, workload calculation |
| CDS Engine | Rule-based allocation recommendations for beds, staff, equipment, resources |
| Forecasting Module | Moving Average and Weighted Moving Average demand prediction |
| Notification Module | System alerts, assignment notifications, low-stock warnings, escalation messages |
| Reporting Module | Operational reports, utilization analytics, audit reports, export capability |
| Audit Logging | Activity tracking, decision logging, compliance record-keeping |
| Authentication | Login, session management, password policies, refresh tokens, login history |
| Administration | User management, system configuration, role management, parameter settings |
| Frontend Dashboard | Real-time overview, interactive data views, recommendation display, override capability |

### 4.2 Out of Scope

| Area | Reason |
|------|--------|
| Billing and Financial Management | Beyond the Lassa Fever Unit's resource allocation mandate |
| Laboratory Information System | Separate system with its own workflow and compliance requirements |
| Pharmacy Management | Handled by existing hospital pharmacy infrastructure |
| Electronic Medical Records (EMR) | System consumes patient data but does not replace full EMR functionality |
| Telemedicine / Remote Consultation | Not a requirement for the current unit's operational model |
| Mobile Application | Web-based interface only for the initial release |
| Multi-Hospital Deployment | Scoped to BSUTH Lassa Fever Unit only |
| AI/ML Predictive Models | Rule-based recommendations initially; machine learning deferred to future phase |
| Integration with NHIS or Insurance Systems | Not relevant to resource allocation scope |
| Hardware Procurement or IoT Sensors | System operates with manually entered or externally provided data |

## 5. Assumptions

1. BSUTH will provide access to a suitable physical or virtual server environment for development and staging.
2. A PostgreSQL database instance will be provisioned and accessible for the development team.
3. Stakeholder interviews and requirements validation sessions will be available during the planning and early development phases.
4. The Lassa Fever Unit currently maintains some form of paper-based or spreadsheet-based records that can inform data migration and system design.
5. Hospital network infrastructure supports internal web application access.
6. Staff members will receive basic training on system usage before go-live.
7. The system will be the primary resource allocation tool for the Lassa Fever Unit; parallel manual processes will be phased out.
8. Patient data entered into the system will be accurate and timely as entered by authorized users.
9. The project will be developed by a small team (2–5 developers) over a defined timeline.
10. COVID-19 and other epidemic preparedness considerations may influence future expansion but are not in scope for the initial release.

## 6. Constraints

| Constraint | Description |
|------------|-------------|
| Technology | Must be implemented using Java (Spring Boot) as the backend language per project requirement. |
| Timeline | Documentation and development must follow a structured roadmap with defined milestones. |
| Budget | Development resources are limited to available team members and existing infrastructure. |
| Data Privacy | Patient health information is governed by Nigerian Data Protection Regulation (NDPR) and hospital privacy policies. |
| Regulatory | System must align with Federal Ministry of Health guidelines for infectious disease management. |
| Internet Dependency | The system requires network connectivity; offline operation is not supported. |
| Browser Support | Frontend must support modern browsers (Chrome, Firefox, Edge) — legacy browser support excluded. |
| Integration | No formal API integrations with external hospital systems in the initial release. |
| Deployment | Docker-based deployment planned for later phase; initial deployment may use traditional methods. |

## 7. Success Criteria

| Criterion | Measure |
|-----------|---------|
| Recommendation Accuracy | ≥ 85% of system recommendations are accepted by staff without override during pilot period. |
| Time Reduction | Average bed-assignment time reduced by ≥ 30% compared to pre-system baseline. |
| System Availability | ≥ 99% uptime during operational hours (8:00 AM – 8:00 PM). |
| User Adoption | ≥ 80% of target users (ward managers, nursing officers, administrators) actively use the system within 3 months of launch. |
| Audit Completeness | 100% of resource allocation decisions are captured in the audit log. |
| Forecast Accuracy | Resource demand forecasts within ±15% of actual demand for the planning horizon. |
| User Satisfaction | Average user satisfaction score ≥ 3.5/5.0 in post-deployment survey. |

## 8. Document References

| Document | Reference |
|----------|-----------|
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| System Architecture | `docs/planning/03-system-architecture.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
| Risk Analysis | `docs/planning/10-risk-analysis.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
