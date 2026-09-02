package com.hospital.resource.resource.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resource_allocations")
public class ResourceAllocation {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "resource_id", nullable = false)
    private UUID resourceId;

    @Column(name = "admission_id", nullable = false)
    private UUID admissionId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "allocated_at", nullable = false, updatable = false)
    private Instant allocatedAt;

    @Column(name = "released_at")
    private Instant releasedAt;

    @Column(name = "allocated_by", nullable = false, updatable = false)
    private UUID allocatedBy;

    public ResourceAllocation() {}

    public ResourceAllocation(UUID id, UUID resourceId, UUID admissionId, Integer quantity, Instant allocatedAt, Instant releasedAt, UUID allocatedBy) {
        this.id = id;
        this.resourceId = resourceId;
        this.admissionId = admissionId;
        this.quantity = quantity;
        this.allocatedAt = allocatedAt != null ? allocatedAt : Instant.now();
        this.releasedAt = releasedAt;
        this.allocatedBy = allocatedBy;
    }

    public static ResourceAllocationBuilder builder() {
        return new ResourceAllocationBuilder();
    }

    public static class ResourceAllocationBuilder {
        private UUID id;
        private UUID resourceId;
        private UUID admissionId;
        private Integer quantity;
        private Instant allocatedAt;
        private Instant releasedAt;
        private UUID allocatedBy;

        public ResourceAllocationBuilder id(UUID id) { this.id = id; return this; }
        public ResourceAllocationBuilder resourceId(UUID resourceId) { this.resourceId = resourceId; return this; }
        public ResourceAllocationBuilder admissionId(UUID admissionId) { this.admissionId = admissionId; return this; }
        public ResourceAllocationBuilder quantity(Integer quantity) { this.quantity = quantity; return this; }
        public ResourceAllocationBuilder allocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; return this; }
        public ResourceAllocationBuilder releasedAt(Instant releasedAt) { this.releasedAt = releasedAt; return this; }
        public ResourceAllocationBuilder allocatedBy(UUID allocatedBy) { this.allocatedBy = allocatedBy; return this; }

        public ResourceAllocation build() {
            return new ResourceAllocation(id, resourceId, admissionId, quantity, allocatedAt, releasedAt, allocatedBy);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (allocatedAt == null) {
            allocatedAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getResourceId() { return resourceId; }
    public void setResourceId(UUID resourceId) { this.resourceId = resourceId; }

    public UUID getAdmissionId() { return admissionId; }
    public void setAdmissionId(UUID admissionId) { this.admissionId = admissionId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Instant getAllocatedAt() { return allocatedAt; }
    public void setAllocatedAt(Instant allocatedAt) { this.allocatedAt = allocatedAt; }

    public Instant getReleasedAt() { return releasedAt; }
    public Instant getReturnedAt() { return releasedAt; }
    public void setReleasedAt(Instant releasedAt) { this.releasedAt = releasedAt; }

    public String getStatus() { return releasedAt != null ? "RELEASED" : "ALLOCATED"; }

    public UUID getAllocatedBy() { return allocatedBy; }
    public void setAllocatedBy(UUID allocatedBy) { this.allocatedBy = allocatedBy; }
}
