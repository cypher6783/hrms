package com.hospital.resource.staff.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "staff")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Staff {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "staff_number", nullable = false, unique = true, length = 20)
    private String staffNumber;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(nullable = false, length = 30)
    private String role;

    @Column(length = 50)
    private String specialization;

    @Column(name = "certification_status", nullable = false, length = 20)
    @Builder.Default
    private String certificationStatus = "CURRENT";

    @Column(name = "certification_expiry")
    private LocalDate certificationExpiry;

    @Column(name = "ward_id")
    private UUID wardId;

    @Column(name = "max_workload_threshold", nullable = false)
    @Builder.Default
    private BigDecimal maxWorkloadThreshold = BigDecimal.ONE;

    @Column(name = "availability_status", nullable = false, length = 20)
    @Builder.Default
    private String availabilityStatus = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Column(name = "created_by", updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    public boolean isAvailable() {
        return "ACTIVE".equals(availabilityStatus);
    }

    public boolean isCertificationCurrent() {
        return certificationExpiry == null || !LocalDate.now().isAfter(certificationExpiry);
    }
}
