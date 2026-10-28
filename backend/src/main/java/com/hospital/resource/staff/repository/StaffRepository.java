package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByWardIdAndAvailabilityStatus(UUID wardId, String availabilityStatus);

    List<Staff> findByRole(String role);

    long countByWardIdAndAvailabilityStatus(UUID wardId, String availabilityStatus);

    long countByAvailabilityStatus(String availabilityStatus);
}
