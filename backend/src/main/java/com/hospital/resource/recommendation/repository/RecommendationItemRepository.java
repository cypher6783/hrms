package com.hospital.resource.recommendation.repository;

import com.hospital.resource.recommendation.entity.RecommendationItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecommendationItemRepository extends JpaRepository<RecommendationItem, UUID> {

    List<RecommendationItem> findByRecommendationId(UUID recommendationId);

    List<RecommendationItem> findByRecommendationIdAndStatus(UUID recommendationId, String status);
}
