# 12 — Open Questions

## 1. Outstanding Requirements

### OQ-01: Patient Data Migration

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Does BSUTH currently maintain any digital patient records (spreadsheets, databases, other systems) that can be migrated into the new system? If so, what format and volume? |
| Impact | Affects data migration strategy, timeline, and Phase 1 deliverables. |
| Required From | BSUTH IT Department / Ward Manager |
| Target Resolution | Before Phase 1 completion |

### OQ-02: Existing Hospital Information Systems

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Are there any existing hospital information systems (HIS) at BSUTH that the new system should integrate with or data should be synchronized with? |
| Impact | Affects system architecture, integration layer design, and future expansion plans. |
| Required From | BSUTH IT Department |
| Target Resolution | Before Phase 2 completion |

### OQ-03: Network Infrastructure

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What is the current network infrastructure at BSUTH? Is there reliable Wi-Fi or LAN connectivity in the Lassa Fever Unit? Are there bandwidth constraints? |
| Impact | Affects deployment strategy, frontend optimization requirements, and offline capability needs. |
| Required From | BSUTH IT Department |
| Target Resolution | Before Phase 5 deployment |

### OQ-04: Hardware Availability

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What hardware (computers, tablets, monitors) is currently available in the Lassa Fever Unit? What additional hardware may be needed? |
| Impact | Affects deployment requirements, UI design constraints, and budget planning. |
| Required From | BSUTH Administration |
| Target Resolution | Before Phase 5 deployment |

---

## 2. Assumptions Requiring Validation

### OQ-05: User Population Size

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | The system assumes a maximum of 50 concurrent users. What is the actual number of staff who will use the system? How many will be simultaneously active? |
| Impact | Affects performance requirements, infrastructure sizing, and licensing considerations. |
| Assumption Made In | `01-project-scope.md`, `03-system-architecture.md` |
| Required From | BSUTH Ward Manager |

### OQ-06: Patient Volume

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | The system assumes up to 10,000 patient records. What is the actual patient volume in the Lassa Fever Unit? How many admissions per month during normal periods vs. outbreak periods? |
| Impact | Affects database sizing, performance requirements, and forecasting model accuracy. |
| Assumption Made In | `01-project-scope.md`, `07-database-plan.md` |
| Required From | BSUTH Ward Manager / Medical Director |

### OQ-07: Severity Classification Standard

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Does BSUTH currently use a standardized severity classification system for Lassa fever patients, or should the system define one? If existing, what are the criteria for each level? |
| Impact | Affects recommendation engine scoring, patient assessment workflows, and clinical integration. |
| Assumption Made In | `06-domain-model.md`, `08-recommendation-engine-design.md` |
| Required From | BSUTH Medical Doctor / Infection Control Officer |

### OQ-08: Isolation Protocol Specifics

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What are the specific isolation protocols for Lassa fever at BSUTH? How many isolation beds are currently available? What are the different isolation levels used? |
| Impact | Affects bed management, ward configuration, and recommendation engine isolation scoring. |
| Assumption Made In | `04-module-breakdown.md`, `07-database-plan.md` |
| Required From | BSUTH Infection Control Officer |

### OQ-09: Staff Roles and Responsibilities

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What are the exact staff roles in the Lassa Fever Unit? How many staff members per role? What are their current shift patterns? |
| Impact | Affects staff management module design, role definitions, and workload calculation. |
| Assumption Made In | `06-domain-model.md`, `08-recommendation-engine-design.md` |
| Required From | BSUTH Ward Manager / Nursing Officer |

### OQ-10: Equipment Inventory

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What medical equipment is currently available in the Lassa Fever Unit? What types and quantities? What is the maintenance schedule? |
| Impact | Affects equipment module design, recommendation engine equipment scoring, and initial data seeding. |
| Assumption Made In | `04-module-breakdown.md`, `06-domain-model.md` |
| Required From | BSUTH Equipment Officer / Biomedical Engineering |

---

## 3. Questions for BSUTH Stakeholders

### OQ-11: Reporting Requirements

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What specific reports does BSUTH management require? What frequency? What format? Are there regulatory reporting requirements that the system should support? |
| Impact | Affects reporting module design and priority. |
| Required From | BSUTH Medical Director / Hospital Management |

### OQ-12: Data Retention Policy

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What is BSUTH's data retention policy for patient records, audit logs, and operational data? Are there regulatory requirements for data retention duration? |
| Impact | Affects database archival strategy, audit log management, and storage requirements. |
| Required From | BSUTH Administration / Legal / Compliance |

### OQ-13: Notification Preferences

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Does BSUTH use email extensively, or are there preferred communication channels (SMS, WhatsApp, internal messaging)? What notification events are most critical? |
| Impact | Affects notification module design and channel selection. |
| Required From | BSUTH Ward Manager / Staff |

### OQ-14: Go-Live Strategy

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Does BSUTH prefer a big-bang go-live or a phased rollout (e.g., start with one ward, then expand)? Is there a preferred go-live date or event? |
| Impact | Affects deployment strategy, training schedule, and rollback planning. |
| Required From | BSUTH Medical Director / Hospital Management |

### OQ-15: Budget Constraints

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | Are there specific budget constraints for infrastructure, hosting, or ongoing operational costs? Does BSUTH have existing server infrastructure or cloud accounts? |
| Impact | Affects deployment infrastructure choices, hosting decisions, and ongoing maintenance planning. |
| Required From | BSUTH Administration / Finance |

### OQ-16: Regulatory Compliance

| Attribute | Detail |
|-----------|--------|
| Status | Open |
| Description | What specific regulatory requirements apply to the system? Are there Nigerian hospital information system standards? NDPR compliance requirements? NCDC reporting mandates? |
| Impact | Affects security implementation, audit logging, and reporting module design. |
| Required From | BSUTH Compliance / Legal / NCDC Liaison |

---

## 4. Potential Future Enhancements

### FHE-01: Mobile Application

| Attribute | Detail |
|-----------|--------|
| Priority | Low |
| Description | Develop a mobile application for nurses and doctors to access the system on smartphones or tablets, enabling bedside data entry and real-time notifications. |
| Dependencies | Successful web application deployment; mobile development resources. |
| Estimated Effort | 8–12 weeks |

### FHE-02: Machine Learning Recommendation Optimization

| Attribute | Detail |
|-----------|--------|
| Priority | Medium |
| Description | Replace or augment rule-based recommendation engine with ML models trained on historical allocation data and outcomes. |
| Dependencies | 12+ months of historical data; data science resources; ML infrastructure. |
| Estimated Effort | 12–16 weeks |

### FHE-03: Integration with NCDC Surveillance System

| Attribute | Detail |
|-----------|--------|
| Priority | Medium |
| Description | Integrate with Nigeria Centre for Disease Control surveillance system for automated case reporting and epidemiological data exchange. |
| Dependencies | NCDC API availability; data sharing agreements; regulatory approval. |
| Estimated Effort | 6–8 weeks |

### FHE-04: IoT Sensor Integration

| Attribute | Detail |
|-----------|--------|
| Priority | Low |
| Description | Integrate IoT sensors for automated bed occupancy detection, environmental monitoring (temperature, humidity), and equipment location tracking. |
| Dependencies | IoT hardware procurement; network infrastructure upgrade; sensor integration APIs. |
| Estimated Effort | 16–24 weeks |

### FHE-05: Multi-Hospital Deployment

| Attribute | Detail |
|-----------|--------|
| Priority | Low |
| Description | Extend the system to support multiple hospitals or units with tenant isolation, shared reporting, and centralized administration. |
| Dependencies | Successful single-hospital deployment; multi-tenant architecture design; hospital partnership agreements. |
| Estimated Effort | 20–30 weeks |

### FHE-06: Telemedicine Integration

| Attribute | Detail |
|-----------|--------|
| Priority | Low |
| Description | Integrate video consultation capabilities for remote specialist consultation on complex Lassa fever cases. |
| Dependencies | Network infrastructure upgrade; video conferencing API; clinical workflow design. |
| Estimated Effort | 10–14 weeks |

### FHE-07: Automated Shift Scheduling

| Attribute | Detail |
|-----------|--------|
| Priority | Medium |
| Description | Implement automated shift scheduling optimization based on staff availability, preferences, workload requirements, and regulatory constraints. |
| Dependencies | Staff management module; historical scheduling data; labor regulation input. |
| Estimated Effort | 8–12 weeks |

### FHE-08: Predictive Analytics Dashboard

| Attribute | Detail |
|-----------|--------|
| Priority | Medium |
| Description | Advanced analytics dashboard with predictive models for outbreak trends, resource depletion, and staffing needs. |
| Dependencies | Historical data accumulation; ML infrastructure; data science expertise. |
| Estimated Effort | 12–16 weeks |

---

## 5. Decision Log

| ID | Decision | Date | Status | Notes |
|----|----------|------|--------|-------|
| D-01 | Use Java Spring Boot for backend | Pre-planning | Decided | Project requirement. |
| D-02 | Use PostgreSQL for database | Pre-planning | Decided | ACID compliance, open-source, healthcare suitability. |
| D-03 | Use React for frontend | Pre-planning | Decided | Component reusability, ecosystem maturity. |
| D-04 | Rule-based recommendation engine initially | Pre-planning | Decided | Predictable behavior; ML deferred to future phase. |
| D-05 | JWT-based authentication | Pre-planning | Decided | Stateless, scalable, industry standard. |
| D-06 | UUID primary keys | Pre-planning | Decided | Distributed uniqueness, security (no sequential ID guessing). |
| D-07 | Soft deletes for patient and admission records | Pre-planning | Decided | Audit trail preservation, historical data integrity. |

---

## 6. Document References

| Document | Reference |
|----------|-----------|
| Project Scope | `docs/planning/01-project-scope.md` |
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Risk Analysis | `docs/planning/10-risk-analysis.md` |
| Development Roadmap | `docs/planning/09-development-roadmap.md` |
