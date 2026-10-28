# 11 — Glossary

## 1. Medical Terms

| Term | Definition |
|------|------------|
| Lassa Fever | A viral hemorrhagic fever caused by the Lassa virus, transmitted through contact with infected rodents or bodily fluids of infected persons. Endemic in West Africa including Nigeria. |
| Hemorrhagic Fever | A group of illnesses caused by several viruses, characterized by fever and bleeding disorders. |
| Viral Load | The amount of virus in a patient's blood, used as a marker of disease severity and infectiousness. |
| Triage | The process of determining the priority of patients' treatments based on the severity of their condition. |
| Severity Assessment | Clinical evaluation of the seriousness of a patient's condition, typically classified as mild, moderate, severe, or critical. |
| Isolation | The separation of patients with contagious diseases to prevent the spread of infection. |
| Positive-Pressure Isolation | An isolation room where air pressure is higher than the corridor, preventing airborne contaminants from entering the room. Used for immunocompromised patients. |
| Negative-Pressure Isolation | An isolation room where air pressure is lower than the corridor, preventing airborne contaminants from escaping the room. Used for patients with airborne infections. |
| Contact Precaution | Infection control measures for patients with diseases transmitted by direct or indirect contact. |
| Droplet Precaution | Infection control measures for patients with diseases transmitted through respiratory droplets. |
| Airborne Precaution | Infection control measures for patients with diseases transmitted through airborne particles. |
| ICU (Intensive Care Unit) | A specialized department providing intensive care medicine for patients with life-threatening conditions. |
| PPE (Personal Protective Equipment) | Protective clothing, gloves, face shields, and masks worn to protect against infection. |
| Decontamination | The process of cleaning and sterilizing equipment and surfaces to remove infectious agents. |
| FEFO (First Expired, First Out) | Inventory management principle where items with the nearest expiration date are used first. |

## 2. Hospital Terminology

| Term | Definition |
|------|------------|
| Ward | A defined section of a hospital where patients are accommodated and cared for. |
| Bed Occupancy | The percentage of hospital beds that are currently occupied by patients. |
| Admission | The process of admitting a patient to a hospital ward for treatment. |
| Discharge | The process of releasing a patient from the hospital after treatment. |
| Transfer | Moving a patient from one ward or bed to another within the hospital. |
| Length of Stay (LOS) | The duration of a single episode of hospitalization for a patient. |
| Bed Turnover | The number of times a bed is occupied by different patients over a period. |
| Staff-to-Patient Ratio | The number of healthcare staff available per patient, used to measure staffing adequacy. |
| Acuity | The severity of a patient's condition; higher acuity requires more intensive care and resources. |
| Census | The total number of patients in a hospital or ward at a given time. |
| Throughput | The number of patients processed through a unit over a defined period. |
| Surge Capacity | The ability of a hospital to expand its treatment capabilities during emergencies or outbreaks. |
| Infection Control | Policies and procedures to prevent the spread of infections within healthcare settings. |
| Next-of-Kin (NOK) | The patient's closest living relative or designated emergency contact. |

## 3. Software Terminology

| Term | Definition |
|------|------------|
| REST API | Representational State Transfer Application Programming Interface; a architectural style for web services. |
| JWT (JSON Web Token) | A compact, URL-safe token format for securely transmitting information between parties. |
| CRUD | Create, Read, Update, Delete; the four basic operations of persistent storage. |
| DTO (Data Transfer Object) | An object that carries data between processes to reduce the number of method calls. |
| ORM (Object-Relational Mapping) | A technique for converting data between incompatible type systems in object-oriented languages and relational databases. |
| JPA (Java Persistence API) | A Java specification for managing relational data in applications. |
| Hibernate | An ORM framework for Java that implements the JPA specification. |
| Spring Boot | An opinionated framework for creating stand-alone Spring-based applications. |
| Spring Security | A powerful and highly customizable authentication and access-control framework for Spring applications. |
| Spring Data JPA | A Spring module that simplifies the implementation of JPA-based data access layers. |
| Flyway | A database migration tool that manages database schema versioning. |
| Transaction | A sequence of operations performed as a single logical unit of work. |
| Soft Delete | A technique where records are marked as deleted rather than physically removed from the database. |
| RBAC (Role-Based Access Control) | A method of restricting system access based on user roles. |
| AOP (Aspect-Oriented Programming) | A programming paradigm that increases modularity by separating cross-cutting concerns. |
| SPA (Single-Page Application) | A web application that loads a single HTML page and dynamically updates content. |
| Virtual Threads | Java 21 feature providing lightweight threads managed by the JVM for improved concurrency. |
| CDN (Content Delivery Network) | A geographically distributed network of servers for delivering content to users. |
| CI/CD (Continuous Integration/Continuous Deployment) | Practices for frequently building, testing, and deploying software. |

## 4. Project Definitions

| Term | Definition |
|------|------------|
| CDS Engine (Clinical Decision Support Engine) | The rule-based system component that generates allocation suggestions for beds, staff, equipment, and resources based on multi-factor weighted scoring. |
| Clinical Assessment | An encounter-specific clinical observation recording a patient's severity, triage classification, and infection status at a point in time. |
| Confidence Score | A numerical value (0.00 to 1.00) indicating the engine's certainty in a recommendation. |
| Override | A user action that replaces a CDS recommendation with an alternative decision, requiring justification. |
| Fallback Logic | Alternative recommendation strategies activated when primary recommendation criteria cannot be satisfied. |
| Escalation | A notification sent to higher-authority users when CDS recommendations are not actioned within defined timeframes. |
| Allocation Recommendation | A recommendation event containing one or more recommendation items for different resource types. |
| Recommendation Item | An individual allocation suggestion (bed, staff, equipment, or resource) within a recommendation event. |
| Recommendation Decision | A user's decision (accept or override) on a recommendation item. |
| Allocation | The assignment of a resource (bed, staff, equipment, consumable) to a specific patient or admission. |
| Inventory Transaction | A record of a stock movement (purchase, issue, return, adjustment, transfer, disposal) in the inventory ledger. |
| Bed Cleaning | A workflow task tracking bed sanitation from assignment through completion and verification. |
| Workload Score | A calculated value representing a staff member's current workload based on patient assignments and severity. |
| Moving Average | A statistical forecasting model that averages recent data points to predict future values. |
| Weighted Moving Average | A forecasting model that gives more weight to recent data points when calculating averages. |
| MAPE (Mean Absolute Percentage Error) | A measure of forecast accuracy calculated as the average of absolute percentage errors. |
| Utilization Rate | The percentage of available resources that are currently in use. |
| Demand Forecast | A prediction of future resource requirements based on historical data and trend analysis. |

## 5. Abbreviations

| Abbreviation | Full Form |
|-------------|-----------|
| BSUTH | Benue State University Teaching Hospital |
| CDS | Clinical Decision Support |
| LassaFR | Lassa Fever Resource (project shorthand) |
| MAPE | Mean Absolute Percentage Error |
| NCDC | Nigeria Centre for Disease Control |
| NDPR | Nigeria Data Protection Regulation |
| NPHCDA | National Primary Health Care Development Agency |
| FMOH | Federal Ministry of Health |
| PHI | Protected Health Information |
| RBAC | Role-Based Access Control |
| TLS | Transport Layer Security |
| JWT | JSON Web Token |
| JPA | Java Persistence API |
| ORM | Object-Relational Mapping |
| DTO | Data Transfer Object |
| CRUD | Create, Read, Update, Delete |
| SPA | Single-Page Application |
| API | Application Programming Interface |
| REST | Representational State Transfer |
| SMA | Simple Moving Average |
| WMA | Weighted Moving Average |
| FEFO | First Expired, First Out |
| PPE | Personal Protective Equipment |
| ICU | Intensive Care Unit |
| LOS | Length of Stay |
| CI/CD | Continuous Integration/Continuous Deployment |
| UAT | User Acceptance Testing |
| RTO | Recovery Time Objective |
| RPO | Recovery Point Objective |
| ICU | Intensive Care Unit |
| PPE | Personal Protective Equipment |
| FEFO | First Expired, First Out |
| LOS | Length of Stay |
| AOP | Aspect-Oriented Programming |
| NFR | Non-Functional Requirement |

---

## 6. Document References

| Document | Reference |
|----------|-----------|
| Project Scope | `docs/planning/01-project-scope.md` |
| Requirements Specification | `docs/planning/02-requirements-specification.md` |
| Module Breakdown | `docs/planning/04-module-breakdown.md` |
