package com.hospital.resource.staff.repository;

import com.hospital.resource.staff.entity.StaffShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface StaffShiftRepository extends JpaRepository<StaffShift, UUID> {

    List<StaffShift> findByWardIdAndShiftDate(UUID wardId, LocalDate shiftDate);

    List<StaffShift> findByShiftDate(LocalDate shiftDate);
}
