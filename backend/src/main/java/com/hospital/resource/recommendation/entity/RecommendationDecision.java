package com.hospital.resource.recommendation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendation_decisions")
public class RecommendationDecision {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "recommendation_item_id", nullable = false)
    private UUID recommendationItemId;

    @Column(name = "decision_type", nullable = false, length = 20)
    private String decisionType;

    @Column(name = "overridden_entity_type", length = 50)
    private String overriddenEntityType;

    @Column(name = "overridden_entity_id")
    private UUID overriddenEntityId;

    @Column(name = "override_justification", columnDefinition = "TEXT")
    private String overrideJustification;

    @Column(name = "decided_by", nullable = false)
    private UUID decidedBy;

    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RecommendationDecision() {}

    public RecommendationDecision(UUID id, UUID recommendationItemId, String decisionType, String overriddenEntityType, UUID overriddenEntityId, String overrideJustification, UUID decidedBy, Instant decidedAt, Instant createdAt) {
        this.id = id;
        this.recommendationItemId = recommendationItemId;
        this.decisionType = decisionType;
        this.overriddenEntityType = overriddenEntityType;
        this.overriddenEntityId = overriddenEntityId;
        this.overrideJustification = overrideJustification;
        this.decidedBy = decidedBy;
        this.decidedAt = decidedAt != null ? decidedAt : Instant.now();
        this.createdAt = createdAt != null ? createdAt : Instant.now();
    }

    public static RecommendationDecisionBuilder builder() {
        return new RecommendationDecisionBuilder();
    }

    public static class RecommendationDecisionBuilder {
        private UUID id;
        private UUID recommendationItemId;
        private String decisionType;
        private String overriddenEntityType;
        private UUID overriddenEntityId;
        private String overrideJustification;
        private UUID decidedBy;
        private Instant decidedAt;
        private Instant createdAt;

        public RecommendationDecisionBuilder id(UUID id) { this.id = id; return this; }
        public RecommendationDecisionBuilder recommendationItemId(UUID recommendationItemId) { this.recommendationItemId = recommendationItemId; return this; }
        public RecommendationDecisionBuilder decisionType(String decisionType) { this.decisionType = decisionType; return this; }
        public RecommendationDecisionBuilder overriddenEntityType(String overriddenEntityType) { this.overriddenEntityType = overriddenEntityType; return this; }
        public RecommendationDecisionBuilder overriddenEntityId(UUID overriddenEntityId) { this.overriddenEntityId = overriddenEntityId; return this; }
        public RecommendationDecisionBuilder overrideJustification(String overrideJustification) { this.overrideJustification = overrideJustification; return this; }
        public RecommendationDecisionBuilder decidedBy(UUID decidedBy) { this.decidedBy = decidedBy; return this; }
        public RecommendationDecisionBuilder decidedAt(Instant decidedAt) { this.decidedAt = decidedAt; return this; }
        public RecommendationDecisionBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public RecommendationDecision build() {
            return new RecommendationDecision(id, recommendationItemId, decisionType, overriddenEntityType, overriddenEntityId, overrideJustification, decidedBy, decidedAt, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
        if (decidedAt == null) decidedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getRecommendationItemId() { return recommendationItemId; }
    public void setRecommendationItemId(UUID recommendationItemId) { this.recommendationItemId = recommendationItemId; }
    public String getDecisionType() { return decisionType; }
    public void setDecisionType(String decisionType) { this.decisionType = decisionType; }
    public String getOverriddenEntityType() { return overriddenEntityType; }
    public void setOverriddenEntityType(String overriddenEntityType) { this.overriddenEntityType = overriddenEntityType; }
    public UUID getOverriddenEntityId() { return overriddenEntityId; }
    public void setOverriddenEntityId(UUID overriddenEntityId) { this.overriddenEntityId = overriddenEntityId; }
    public String getOverrideJustification() { return overrideJustification; }
    public void setOverrideJustification(String overrideJustification) { this.overrideJustification = overrideJustification; }
    public UUID getDecidedBy() { return decidedBy; }
    public void setDecidedBy(UUID decidedBy) { this.decidedBy = decidedBy; }
    public Instant getDecidedAt() { return decidedAt; }
    public void setDecidedAt(Instant decidedAt) { this.decidedAt = decidedAt; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
