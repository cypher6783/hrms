package com.hospital.resource.bedcleaning.repository;

import com.hospital.resource.bedcleaning.entity.BedCleaning;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BedCleaningRepository extends JpaRepository<BedCleaning, UUID> {

    List<BedCleaning> findByBedIdAndStatus(UUID bedId, String status);

    List<BedCleaning> findByStatus(String status);

    Optional<BedCleaning> findByAdmissionIdAndStatus(UUID admissionId, String status);

    List<BedCleaning> findByAssignedTo(UUID assignedTo);
}
