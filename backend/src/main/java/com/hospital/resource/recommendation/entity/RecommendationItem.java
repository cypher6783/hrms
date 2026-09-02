package com.hospital.resource.recommendation.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "recommendation_items")
public class RecommendationItem {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", type = org.hibernate.id.UUIDGenerator.class)
    private UUID id;

    @Column(name = "recommendation_id", nullable = false)
    private UUID recommendationId;

    @Column(name = "item_type", nullable = false, length = 20)
    private String itemType;

    @Column(name = "recommended_entity_type", nullable = false, length = 50)
    private String recommendedEntityType;

    @Column(name = "recommended_entity_id", nullable = false)
    private UUID recommendedEntityId;

    @Column(nullable = false)
    private Integer rank = 1;

    @Column(name = "confidence_score", nullable = false, precision = 3, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "scoring_breakdown", columnDefinition = "jsonb")
    private String scoringBreakdown;

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    public RecommendationItem() {}

    public RecommendationItem(UUID id, UUID recommendationId, String itemType, String recommendedEntityType, UUID recommendedEntityId, Integer rank, BigDecimal confidenceScore, String scoringBreakdown, String rationale, String status, Instant createdAt) {
        this.id = id;
        this.recommendationId = recommendationId;
        this.itemType = itemType;
        this.recommendedEntityType = recommendedEntityType;
        this.recommendedEntityId = recommendedEntityId;
        this.rank = rank != null ? rank : 1;
        this.confidenceScore = confidenceScore;
        this.scoringBreakdown = scoringBreakdown;
        this.rationale = rationale;
        this.status = status != null ? status : "PENDING";
        this.createdAt = createdAt;
    }

    public static RecommendationItemBuilder builder() {
        return new RecommendationItemBuilder();
    }

    public static class RecommendationItemBuilder {
        private UUID id;
        private UUID recommendationId;
        private String itemType;
        private String recommendedEntityType;
        private UUID recommendedEntityId;
        private Integer rank = 1;
        private BigDecimal confidenceScore;
        private String scoringBreakdown;
        private String rationale;
        private String status = "PENDING";
        private Instant createdAt;

        public RecommendationItemBuilder id(UUID id) { this.id = id; return this; }
        public RecommendationItemBuilder recommendationId(UUID recommendationId) { this.recommendationId = recommendationId; return this; }
        public RecommendationItemBuilder itemType(String itemType) { this.itemType = itemType; return this; }
        public RecommendationItemBuilder recommendedEntityType(String recommendedEntityType) { this.recommendedEntityType = recommendedEntityType; return this; }
        public RecommendationItemBuilder recommendedEntityId(UUID recommendedEntityId) { this.recommendedEntityId = recommendedEntityId; return this; }
        public RecommendationItemBuilder rank(Integer rank) { this.rank = rank; return this; }
        public RecommendationItemBuilder confidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; return this; }
        public RecommendationItemBuilder scoringBreakdown(String scoringBreakdown) { this.scoringBreakdown = scoringBreakdown; return this; }
        public RecommendationItemBuilder rationale(String rationale) { this.rationale = rationale; return this; }
        public RecommendationItemBuilder status(String status) { this.status = status; return this; }
        public RecommendationItemBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }

        public RecommendationItem build() {
            return new RecommendationItem(id, recommendationId, itemType, recommendedEntityType, recommendedEntityId, rank, confidenceScore, scoringBreakdown, rationale, status, createdAt);
        }
    }

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getRecommendationId() { return recommendationId; }
    public void setRecommendationId(UUID recommendationId) { this.recommendationId = recommendationId; }

    public String getItemType() { return itemType; }
    public void setItemType(String itemType) { this.itemType = itemType; }

    public String getRecommendedEntityType() { return recommendedEntityType; }
    public void setRecommendedEntityType(String recommendedEntityType) { this.recommendedEntityType = recommendedEntityType; }

    public UUID getRecommendedEntityId() { return recommendedEntityId; }
    public void setRecommendedEntityId(UUID recommendedEntityId) { this.recommendedEntityId = recommendedEntityId; }

    public Integer getRank() { return rank; }
    public void setRank(Integer rank) { this.rank = rank; }

    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(BigDecimal confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getScoringBreakdown() { return scoringBreakdown; }
    public void setScoringBreakdown(String scoringBreakdown) { this.scoringBreakdown = scoringBreakdown; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
