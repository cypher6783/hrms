package com.hospital.resource.ward.service;

import com.hospital.resource.common.dto.PagedResponse;
import com.hospital.resource.common.exception.BusinessException;
import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.ward.domain.WardDomainService;
import com.hospital.resource.ward.dto.WardRequest;
import com.hospital.resource.ward.dto.WardResponse;
import com.hospital.resource.ward.dto.WardStatusResponse;
import com.hospital.resource.ward.entity.Ward;
import com.hospital.resource.ward.mapper.WardMapper;
import com.hospital.resource.ward.repository.WardRepository;
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
public class WardApplicationService {

    private final WardRepository wardRepository;
    private final WardMapper wardMapper;
    private final WardDomainService wardDomainService;

    @Transactional
    public WardResponse createWard(WardRequest request, UUID userId) {
        if (wardRepository.existsByName(request.name())) {
            throw new BusinessException("Ward with name '" + request.name() + "' already exists");
        }

        Ward ward = wardMapper.toEntity(request);
        ward.setStatus("ACTIVE");
        ward.setCreatedBy(userId);
        ward.setUpdatedBy(userId);

        ward = wardRepository.save(ward);
        log.info("Ward created: wardId={}, name={}", ward.getId(), ward.getName());
        return wardMapper.toResponse(ward);
    }

    @Transactional(readOnly = true)
    public WardResponse getWard(UUID id) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward", id.toString()));
        return wardMapper.toResponse(ward);
    }

    @Transactional(readOnly = true)
    public List<WardResponse> getAllActiveWards() {
        List<Ward> wards = wardRepository.findByStatus("ACTIVE");
        return wardMapper.toResponseList(wards);
    }

    @Transactional
    public WardResponse updateWard(UUID id, WardRequest request, UUID userId) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward", id.toString()));

        if (!ward.getName().equals(request.name()) && wardRepository.existsByName(request.name())) {
            throw new BusinessException("Ward with name '" + request.name() + "' already exists");
        }

        wardMapper.updateEntity(request, ward);
        ward.setUpdatedBy(userId);

        ward = wardRepository.save(ward);
        log.info("Ward updated: wardId={}", ward.getId());
        return wardMapper.toResponse(ward);
    }

    @Transactional
    public void deactivateWard(UUID id, UUID userId) {
        Ward ward = wardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ward", id.toString()));
        ward.setStatus("INACTIVE");
        ward.setUpdatedBy(userId);
        wardRepository.save(ward);
        log.info("Ward deactivated: wardId={}", id);
    }

    @Transactional(readOnly = true)
    public long getActiveWardCount() {
        return wardRepository.findByStatus("ACTIVE").size();
    }

    @Transactional(readOnly = true)
    public WardStatusResponse getWardStatus(UUID wardId) {
        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new ResourceNotFoundException("Ward", wardId.toString()));

        long totalBeds = wardDomainService.calculateTotalBeds(wardId);
        long availableBeds = wardDomainService.calculateAvailableBeds(wardId);
        long occupiedBeds = wardDomainService.calculateOccupiedBeds(wardId);
        long cleaningBeds = wardDomainService.calculateCleaningBeds(wardId);
        double occupancyRate = wardDomainService.calculateOccupancyRate(ward);

        return wardMapper.toStatusResponse(ward, totalBeds, availableBeds, occupiedBeds, cleaningBeds, occupancyRate);
    }

    @Transactional(readOnly = true)
    public PagedResponse<WardResponse> searchWards(String search, int page, int size) {
        List<Ward> wards = wardRepository.searchWards(search != null ? search : "");

        int start = page * size;
        int end = Math.min(start + size, wards.size());
        List<WardResponse> content = wardMapper.toResponseList(wards.subList(start, end));

        return PagedResponse.of(content, page, size, wards.size());
    }
}
