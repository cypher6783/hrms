package com.hospital.resource.staff.service;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.staff.dto.StaffRequest;
import com.hospital.resource.staff.dto.StaffResponse;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.repository.StaffRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffApplicationService {

    private final StaffRepository staffRepository;

    @Transactional
    public StaffResponse createStaff(StaffRequest request, UUID userId) {
        Staff staff = Staff.builder()
                .staffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .fullName(request.fullName())
                .role(request.role())
                .specialization(request.specialization())
                .certificationStatus(request.certificationStatus() != null ? request.certificationStatus() : "CURRENT")
                .certificationExpiry(request.certificationExpiry())
                .wardId(request.wardId())
                .maxWorkloadThreshold(request.maxWorkloadThreshold() != null ? request.maxWorkloadThreshold() : java.math.BigDecimal.ONE)
                .availabilityStatus(request.availabilityStatus() != null ? request.availabilityStatus() : "ACTIVE")
                .createdBy(userId)
                .updatedBy(userId)
                .build();

        staff = staffRepository.save(staff);
        log.info("Staff created: staffId={}, staffNumber={}", staff.getId(), staff.getStaffNumber());
        return toResponse(staff);
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaff(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));
        return toResponse(staff);
    }

    @Transactional(readOnly = true)
    public PagedResponse<StaffResponse> getAllStaff(int page, int size) {
        Page<Staff> staffPage = staffRepository.findAll(
                PageRequest.of(page, size, Sort.by("fullName").ascending())
        );
        return PagedResponse.of(
                staffPage.getContent().stream().map(this::toResponse).toList(),
                page, size, staffPage.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public List<StaffResponse> getStaffByWard(UUID wardId) {
        return staffRepository.findByWardIdAndAvailabilityStatus(wardId, "ACTIVE").stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public StaffResponse updateStaff(UUID id, StaffRequest request, UUID userId) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));

        staff.setFullName(request.fullName());
        staff.setRole(request.role());
        staff.setSpecialization(request.specialization());
        staff.setCertificationStatus(request.certificationStatus());
        staff.setCertificationExpiry(request.certificationExpiry());
        staff.setWardId(request.wardId());
        staff.setAvailabilityStatus(request.availabilityStatus());
        staff.setUpdatedBy(userId);

        staff = staffRepository.save(staff);
        log.info("Staff updated: staffId={}", staff.getId());
        return toResponse(staff);
    }

    @Transactional(readOnly = true)
    public long getActiveStaffCount() {
        return staffRepository.countByAvailabilityStatus("ACTIVE");
    }

    private StaffResponse toResponse(Staff staff) {
        return new StaffResponse(
                staff.getId(),
                staff.getStaffNumber(),
                staff.getFullName(),
                staff.getRole(),
                staff.getSpecialization(),
                staff.getCertificationStatus(),
                staff.getCertificationExpiry(),
                staff.getWardId(),
                staff.getMaxWorkloadThreshold(),
                staff.getAvailabilityStatus(),
                staff.getCreatedAt(),
                staff.getUpdatedAt()
        );
    }
}
