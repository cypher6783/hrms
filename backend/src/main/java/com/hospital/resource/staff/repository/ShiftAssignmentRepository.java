package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.ShiftAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, UUID> {

    List<ShiftAssignment> findByStaffId(UUID staffId);

    List<ShiftAssignment> findByShiftId(UUID shiftId);

    long countByShiftId(UUID shiftId);

    boolean existsByStaffIdAndShiftId(UUID staffId, UUID shiftId);

    @Query("SELECT sa FROM ShiftAssignment sa WHERE sa.staffId = :staffId AND sa.shiftId IN " +
            "(SELECT ss.id FROM StaffShift ss WHERE ss.shiftDate = :shiftDate)")
    List<ShiftAssignment> findByStaffIdAndShiftDate(@Param("staffId") UUID staffId, @Param("shiftDate") LocalDate shiftDate);

    @Query("SELECT sa FROM ShiftAssignment sa JOIN StaffShift ss ON sa.shiftId = ss.id " +
            "WHERE sa.staffId = :staffId AND ss.shiftDate = :shiftDate " +
            "AND ss.startTime < :endTime AND ss.endTime > :startTime")
    List<ShiftAssignment> findOverlappingAssignments(
            @Param("staffId") UUID staffId,
            @Param("shiftDate") LocalDate shiftDate,
            @Param("startTime") java.time.LocalTime startTime,
            @Param("endTime") java.time.LocalTime endTime
    );

    @Query("SELECT COUNT(sa) FROM ShiftAssignment sa WHERE sa.staffId = :staffId AND sa.status = 'CONFIRMED'")
    long countConfirmedAssignmentsByStaff(@Param("staffId") UUID staffId);

    void deleteByShiftId(UUID shiftId);
}
