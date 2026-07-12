package com.hospital.resource.recommendation.repository;

import com.hospital.resource.recommendation.entity.RecommendationDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RecommendationDecisionRepository extends JpaRepository<RecommendationDecision, UUID> {

    RecommendationDecision findByRecommendationItemId(UUID recommendationItemId);
}
