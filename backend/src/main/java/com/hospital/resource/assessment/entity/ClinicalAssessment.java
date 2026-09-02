package com.hospital.resource.assessment.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "clinical_assessments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClinicalAssessment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "patient_id", nullable = false)
    private UUID patientId;

    @Column(name = "admission_id")
    private UUID admissionId;

    @Column(name = "assessed_by", nullable = false)
    private UUID assessedBy;

    @Column(name = "severity_level", nullable = false, length = 20)
    private String severityLevel;

    @Column(name = "triage_classification", nullable = false, length = 20)
    private String triageClassification;

    @Column(name = "infection_status", nullable = false, length = 20)
    private String infectionStatus;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "is_reassessment", nullable = false)
    @Builder.Default
    private Boolean isReassessment = false;

    @Column(name = "assessment_timestamp", nullable = false)
    private Instant assessmentTimestamp;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
