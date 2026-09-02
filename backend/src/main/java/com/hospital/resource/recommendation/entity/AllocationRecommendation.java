package com.hospital.resource.recommendation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "allocation_recommendations")
public class AllocationRecommendation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "admission_id", nullable = false)
    private UUID admissionId;

    @Column(name = "batch_type", nullable = false, length = 30)
    private String batchType;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "generated_at", nullable = false)
    private Instant generatedAt;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public AllocationRecommendation() {}

    public AllocationRecommendation(UUID id, UUID admissionId, String batchType, String status, Instant generatedAt, Instant expiresAt, Instant createdAt) {
        this.id = id;
        this.admissionId = admissionId;
        this.batchType = batchType;
        this.status = status != null ? status : "PENDING";
        this.generatedAt = generatedAt;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static AllocationRecommendationBuilder builder() {
        return new AllocationRecommendationBuilder();
    }

    public static class AllocationRecommendationBuilder {
        private UUID id;
        private UUID admissionId;
        private String batchType;
        private String status = "PENDING";
        private Instant generatedAt;
        private Instant expiresAt;
        private Instant createdAt;

        public AllocationRecommendationBuilder id(UUID id) { this.id = id; return this; }
        public AllocationRecommendationBuilder admissionId(UUID admissionId) { this.admissionId = admissionId; return this; }
        public AllocationRecommendationBuilder batchType(String batchType) { this.batchType = batchType; return this; }
        public AllocationRecommendationBuilder status(String status) { this.status = status; return this; }
        public AllocationRecommendationBuilder generatedAt(Instant generatedAt) { this.generatedAt = generatedAt; return this; }
        public AllocationRecommendationBuilder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }
        public AllocationRecommendationBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public AllocationRecommendation build() {
            return new AllocationRecommendation(id, admissionId, batchType, status, generatedAt, expiresAt, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAdmissionId() { return admissionId; }
    public void setAdmissionId(UUID admissionId) { this.admissionId = admissionId; }

    public String getBatchType() { return batchType; }
    public void setBatchType(String batchType) { this.batchType = batchType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(Instant generatedAt) { this.generatedAt = generatedAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
