package com.hospital.resource.bed.service;

import com.hospital.resource.bed.domain.BedDomainService;
import com.hospital.resource.bed.dto.BedAvailabilityResponse;
import com.hospital.resource.bed.dto.BedRequest;
import com.hospital.resource.bed.dto.BedResponse;
import com.hospital.resource.bed.entity.Bed;
import com.hospital.resource.bed.mapper.BedMapper;
import com.hospital.resource.bed.repository.BedRepository;
import com.hospital.resource.common.exception.BusinessException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BedApplicationService {

    private final BedRepository bedRepository;
    private final BedMapper bedMapper;
    private final BedDomainService bedDomainService;

    @Transactional
    public BedResponse createBed(BedRequest request, UUID userId) {
        boolean existsInWard = bedRepository.findByWardId(request.wardId()).stream()
                .anyMatch(b -> b.getBedNumber().equals(request.bedNumber()));
        if (existsInWard) {
            throw new BusinessException("Bed number '" + request.bedNumber() + "' already exists in this ward");
        }

        Bed bed = bedMapper.toEntity(request);
        bed.setStatus("AVAILABLE");
        bed.setCreatedBy(userId);
        bed.setUpdatedBy(userId);

        bed = bedRepository.save(bed);
        log.info("Bed created: bedId={}, wardId={}, bedNumber={}", bed.getId(), bed.getWardId(), bed.getBedNumber());
        return bedMapper.toResponse(bed);
    }

    @Transactional(readOnly = true)
    public BedResponse getBed(UUID id) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", id.toString()));
        return bedMapper.toResponse(bed);
    }

    @Transactional(readOnly = true)
    public List<BedResponse> getBedsByWard(UUID wardId) {
        List<Bed> beds = bedRepository.findByWardId(wardId);
        return bedMapper.toResponseList(beds);
    }

    @Transactional(readOnly = true)
    public List<BedResponse> getAvailableIsolationBeds() {
        List<Bed> beds = bedRepository.findByStatusAndIsIsolationCapable("AVAILABLE", true);
        return bedMapper.toResponseList(beds);
    }

    @Transactional(readOnly = true)
    public BedAvailabilityResponse getBedAvailability(UUID wardId) {
        BedDomainService.BedAvailabilitySummary summary = bedDomainService.getAvailabilitySummary(wardId);

        return new BedAvailabilityResponse(
                wardId,
                null,
                (int) summary.totalBeds(),
                (int) summary.availableBeds(),
                0,
                (int) summary.occupiedBeds()
        );
    }

    @Transactional
    public BedResponse updateBed(UUID id, BedRequest request, UUID userId) {
        Bed bed = bedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", id.toString()));

        if (!bed.getBedNumber().equals(request.bedNumber())) {
            boolean existsInWard = bedRepository.findByWardId(bed.getWardId()).stream()
                    .anyMatch(b -> b.getBedNumber().equals(request.bedNumber()) && !b.getId().equals(id));
            if (existsInWard) {
                throw new BusinessException("Bed number '" + request.bedNumber() + "' already exists in this ward");
            }
        }

        bedMapper.updateEntity(request, bed);
        bed.setUpdatedBy(userId);

        bed = bedRepository.save(bed);
        log.info("Bed updated: bedId={}", bed.getId());
        return bedMapper.toResponse(bed);
    }

    @Transactional
    public BedResponse updateBedStatus(UUID bedId, String newStatus) {
        Bed bed = bedRepository.findById(bedId)
                .orElseThrow(() -> new ResourceNotFoundException("Bed", bedId.toString()));

        bedDomainService.validateStatusTransition(bed.getStatus(), newStatus);

        bed.setStatus(newStatus);
        bed = bedRepository.save(bed);
        log.info("Bed status updated: bedId={}, newStatus={}", bedId, newStatus);
        return bedMapper.toResponse(bed);
    }

    @Transactional(readOnly = true)
    public List<BedResponse> filterBeds(UUID wardId, String bedType, String status, Boolean isIsolationCapable) {
        List<Bed> beds = bedRepository.findBedsWithFilters(wardId, bedType, status, isIsolationCapable);
        return bedMapper.toResponseList(beds);
    }
}
