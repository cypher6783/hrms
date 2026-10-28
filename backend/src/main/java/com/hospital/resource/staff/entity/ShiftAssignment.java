package com.hospital.resource.staff.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "shift_assignments", uniqueConstraints = @UniqueConstraint(columnNames = {"staff_id", "shift_id"}))
public class ShiftAssignment {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "staff_id", nullable = false)
    private UUID staffId;

    @Column(name = "shift_id", nullable = false)
    private UUID shiftId;

    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";

    @Column(name = "assigned_by", nullable = false)
    private UUID assignedBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public ShiftAssignment() {}

    public ShiftAssignment(UUID id, UUID staffId, UUID shiftId, String status, UUID assignedBy, Instant createdAt) {
        this.id = id;
        this.staffId = staffId;
        this.shiftId = shiftId;
        this.status = status != null ? status : "CONFIRMED";
        this.assignedBy = assignedBy;
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static ShiftAssignmentBuilder builder() {
        return new ShiftAssignmentBuilder();
    }

    public static class ShiftAssignmentBuilder {
        private UUID id;
        private UUID staffId;
        private UUID shiftId;
        private String status = "CONFIRMED";
        private UUID assignedBy;
        private Instant createdAt;

        public ShiftAssignmentBuilder id(UUID id) { this.id = id; return this; }
        public ShiftAssignmentBuilder staffId(UUID staffId) { this.staffId = staffId; return this; }
        public ShiftAssignmentBuilder shiftId(UUID shiftId) { this.shiftId = shiftId; return this; }
        public ShiftAssignmentBuilder status(String status) { this.status = status; return this; }
        public ShiftAssignmentBuilder assignedBy(UUID assignedBy) { this.assignedBy = assignedBy; return this; }
        public ShiftAssignmentBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public ShiftAssignment build() {
            return new ShiftAssignment(id, staffId, shiftId, status, assignedBy, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStaffId() { return staffId; }
    public void setStaffId(UUID staffId) { this.staffId = staffId; }

    public UUID getShiftId() { return shiftId; }
    public void setShiftId(UUID shiftId) { this.shiftId = shiftId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public UUID getAssignedBy() { return assignedBy; }
    public void setAssignedBy(UUID assignedBy) { this.assignedBy = assignedBy; }

    public Instant getCreatedAt() { return createdAt; }
    public Instant getAssignedAt() { return createdAt != null ? createdAt : Instant.now(); }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
