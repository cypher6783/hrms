package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.Staff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StaffRepository extends JpaRepository<Staff, UUID> {

    List<Staff> findByWardIdAndAvailabilityStatus(UUID wardId, String availabilityStatus);

    List<Staff> findByRole(String role);

    long countByWardIdAndAvailabilityStatus(UUID wardId, String availabilityStatus);

    long countByAvailabilityStatus(String availabilityStatus);

    Page<Staff> findByFullNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT s FROM Staff s WHERE (:name IS NULL OR LOWER(s.fullName) LIKE LOWER(CONCAT('%', :name, '%'))) " +
            "AND (:role IS NULL OR s.role = :role) " +
            "AND (:specialization IS NULL OR s.specialization = :specialization) " +
            "AND (:wardId IS NULL OR s.wardId = :wardId) " +
            "AND (:availabilityStatus IS NULL OR s.availabilityStatus = :availabilityStatus) " +
            "AND (:certificationStatus IS NULL OR s.certificationStatus = :certificationStatus)")
    Page<Staff> searchStaff(
            @Param("name") String name,
            @Param("role") String role,
            @Param("specialization") String specialization,
            @Param("wardId") UUID wardId,
            @Param("availabilityStatus") String availabilityStatus,
            @Param("certificationStatus") String certificationStatus,
            Pageable pageable
    );

    @Query("SELECT COUNT(s) FROM Staff s WHERE s.wardId = :wardId")
    long countByWardId(@Param("wardId") UUID wardId);

    @Query("SELECT s.role, COUNT(s) FROM Staff s WHERE s.wardId = :wardId GROUP BY s.role")
    List<Object[]> countByRoleForWard(@Param("wardId") UUID wardId);

    @Query("SELECT COUNT(s) FROM Staff s WHERE s.certificationExpiry IS NOT NULL AND s.certificationExpiry < CURRENT_DATE")
    long countWithExpiredCertification();

    List<Staff> findByWardId(UUID wardId);
}
