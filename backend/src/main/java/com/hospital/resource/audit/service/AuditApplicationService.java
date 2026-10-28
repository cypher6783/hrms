package com.hospital.resource.audit.service;

import com.hospital.resource.audit.dto.AuditLogResponse;
import com.hospital.resource.audit.entity.AuditLog;
import com.hospital.resource.audit.repository.AuditLogRepository;
import com.hospital.resource.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditApplicationService {

    private final AuditLogRepository auditLogRepository;

    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAuditLogsByEntity(String entityType, UUID entityId, int page, int size) {
        Page<AuditLog> logs = auditLogRepository.findByEntityTypeAndEntityId(entityType, entityId,
                PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return PagedResponse.of(
                logs.getContent().stream().map(this::toResponse).toList(),
                page, size, logs.getTotalElements()
        );
    }

    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAuditLogsByUser(UUID userId, int page, int size) {
        Page<AuditLog> logs = auditLogRepository.findByUserId(userId,
                PageRequest.of(page, size, Sort.by("timestamp").descending()));
        return PagedResponse.of(
                logs.getContent().stream().map(this::toResponse).toList(),
                page, size, logs.getTotalElements()
        );
    }

    private AuditLogResponse toResponse(AuditLog log) {
        return new AuditLogResponse(
                log.getId(), log.getTimestamp(), log.getUserId(),
                log.getActionType(), log.getEntityType(), log.getEntityId(),
                log.getIpAddress(), log.getCreatedAt()
        );
    }
}
