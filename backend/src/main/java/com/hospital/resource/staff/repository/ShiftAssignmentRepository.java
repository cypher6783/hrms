package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, UUID> {

    List<ShiftAssignment> findByStaffId(UUID staffId);

    List<ShiftAssignment> findByShiftId(UUID shiftId);

    long countByShiftId(UUID shiftId);

    boolean existsByStaffIdAndShiftId(UUID staffId, UUID shiftId);
}
