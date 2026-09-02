package com.hospital.resource.staff.service;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.event.DomainEventPublisher;
import com.hospital.resource.common.event.staff.StaffAssignedEvent;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.staff.domain.WorkloadCalculator;
import com.hospital.resource.staff.dto.*;
import com.hospital.resource.staff.entity.Staff;
import com.hospital.resource.staff.mapper.StaffMapper;
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
    private final WorkloadCalculator workloadCalculator;
    private final StaffMapper staffMapper;
    private final DomainEventPublisher eventPublisher;

    @Transactional
    public StaffResponse createStaff(StaffRequest request, UUID userId) {
        Staff staff = staffMapper.toEntity(request);
        staff.setStaffNumber("STF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        staff.setCertificationStatus(request.certificationStatus() != null ? request.certificationStatus() : "CURRENT");
        staff.setMaxWorkloadThreshold(request.maxWorkloadThreshold() != null ? request.maxWorkloadThreshold() : java.math.BigDecimal.ONE);
        staff.setAvailabilityStatus(request.availabilityStatus() != null ? request.availabilityStatus() : "ACTIVE");
        staff.setCreatedBy(userId);
        staff.setUpdatedBy(userId);

        staff = staffRepository.save(staff);

        if (staff.getWardId() != null) {
            eventPublisher.publish(new StaffAssignedEvent(this, staff.getId(), staff.getWardId()));
        }

        log.info("Staff created: staffId={}, staffNumber={}", staff.getId(), staff.getStaffNumber());
        return staffMapper.toResponse(staff);
    }

    @Transactional(readOnly = true)
    public StaffResponse getStaff(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));
        return staffMapper.toResponse(staff);
    }

    @Transactional(readOnly = true)
    public StaffSummaryResponse getStaffSummary(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));
        return staffMapper.toSummary(staff);
    }

    @Transactional(readOnly = true)
    public PagedResponse<StaffResponse> getAllStaff(int page, int size) {
        Page<Staff> staffPage = staffRepository.findAll(
                PageRequest.of(page, size, Sort.by("fullName").ascending())
        );
        return PagedResponse.of(
                staffPage.getContent().stream().map(staffMapper::toResponse).toList(),
                page, size, staffPage.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<StaffResponse> searchStaff(StaffSearchRequest request) {
        PageRequest pageRequest = PageRequest.of(request.page(), request.size(),
                Sort.by("fullName").ascending());

        Page<Staff> page = staffRepository.searchStaff(
                request.name(), request.role(), request.specialization(),
                request.wardId(), request.availabilityStatus(), request.certificationStatus(),
                pageRequest);

        List<StaffResponse> content = page.getContent().stream()
                .map(staffMapper::toResponse)
                .toList();

        return PagedResponse.of(content, request.page(), request.size(), page.getTotalElements());
    }

    @Transactional(readOnly = true)
    public List<StaffSummaryResponse> getStaffByWard(UUID wardId) {
        return staffRepository.findByWardIdAndAvailabilityStatus(wardId, "ACTIVE").stream()
                .map(staffMapper::toSummary)
                .toList();
    }

    @Transactional
    public StaffResponse updateStaff(UUID id, StaffRequest request, UUID userId) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));

        staffMapper.updateEntity(request, staff);
        staff.setUpdatedBy(userId);

        staff = staffRepository.save(staff);
        log.info("Staff updated: staffId={}", staff.getId());
        return staffMapper.toResponse(staff);
    }

    @Transactional(readOnly = true)
    public StaffWorkloadResponse getStaffWorkload(UUID id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff", id.toString()));
        return workloadCalculator.calculateWorkload(staff);
    }

    @Transactional(readOnly = true)
    public StaffStatsResponse getStaffStats() {
        long activeCount = staffRepository.countByAvailabilityStatus("ACTIVE");
        long inactiveCount = staffRepository.countByAvailabilityStatus("INACTIVE");
        long onLeaveCount = staffRepository.countByAvailabilityStatus("ON_LEAVE");
        long expiredCerts = staffRepository.countWithExpiredCertification();
        return new StaffStatsResponse(activeCount, inactiveCount, onLeaveCount, expiredCerts);
    }

    @Transactional(readOnly = true)
    public long getActiveStaffCount() {
        return staffRepository.countByAvailabilityStatus("ACTIVE");
    }

    public record StaffStatsResponse(
            long activeStaff,
            long inactiveStaff,
            long onLeaveStaff,
            long expiredCertifications
    ) {}
}
