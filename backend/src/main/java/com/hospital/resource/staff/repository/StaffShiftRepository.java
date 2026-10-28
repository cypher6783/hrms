package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.StaffShift;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, UUID> {

    List<StaffShift> findByWardIdAndShiftDate(UUID wardId, LocalDate shiftDate);

    List<StaffShift> findByShiftDate(LocalDate shiftDate);

    @Query("SELECT ss FROM StaffShift ss WHERE (:wardId IS NULL OR ss.wardId = :wardId) " +
            "AND (:shiftDateFrom IS NULL OR ss.shiftDate >= :shiftDateFrom) " +
            "AND (:shiftDateTo IS NULL OR ss.shiftDate <= :shiftDateTo) " +
            "AND (:shiftName IS NULL OR ss.shiftName = :shiftName) " +
            "AND (:status IS NULL OR ss.status = :status)")
    Page<StaffShift> searchShifts(
            @Param("wardId") UUID wardId,
            @Param("shiftDateFrom") LocalDate shiftDateFrom,
            @Param("shiftDateTo") LocalDate shiftDateTo,
            @Param("shiftName") String shiftName,
            @Param("status") String status,
            Pageable pageable
    );

    @Query("SELECT ss FROM StaffShift ss WHERE ss.shiftDate BETWEEN :startDate AND :endDate ORDER BY ss.shiftDate, ss.startTime")
    List<StaffShift> findShiftsInDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT ss FROM StaffShift ss WHERE ss.wardId = :wardId AND ss.shiftDate BETWEEN :startDate AND :endDate ORDER BY ss.shiftDate, ss.startTime")
    List<StaffShift> findShiftsByWardAndDateRange(@Param("wardId") UUID wardId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT COUNT(ss) FROM StaffShift ss WHERE ss.shiftDate = :date")
    long countByShiftDate(@Param("date") LocalDate date);

    @Query("SELECT ss.status, COUNT(ss) FROM StaffShift ss GROUP BY ss.status")
    List<Object[]> countByStatusGrouped();
}
