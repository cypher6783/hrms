package com.hospital.resource.recommendation.repository;

import com.hospital.resource.recommendation.entity.AllocationRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AllocationRecommendationRepository extends JpaRepository<AllocationRecommendation, UUID> {

    List<AllocationRecommendation> findByAdmissionIdAndStatus(UUID admissionId, String status);

    List<AllocationRecommendation> findByStatus(String status);
}
