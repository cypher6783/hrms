package com.hospital.resource.audit.service;

import com.hospital.resource.audit.dto.AuditLogResponse;
import com.hospital.resource.audit.entity.AuditLog;
import com.hospital.resource.audit.repository.AuditLogRepository;
import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.UserRepository;
import com.hospital.resource.common.dto.PagedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.HexFormat;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditApplicationService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PagedResponse<AuditLogResponse> getAllAuditLogs(int page, int size, String actionType, String entityType) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        Page<AuditLog> logs;

        if (actionType != null && !actionType.isBlank() && entityType != null && !entityType.isBlank()) {
            logs = auditLogRepository.findByActionTypeAndEntityType(actionType.trim(), entityType.trim(), pageRequest);
        } else if (actionType != null && !actionType.isBlank()) {
            logs = auditLogRepository.findByActionType(actionType.trim(), pageRequest);
        } else if (entityType != null && !entityType.isBlank()) {
            logs = auditLogRepository.findByEntityType(entityType.trim(), pageRequest);
        } else {
            logs = auditLogRepository.findAll(pageRequest);
        }

        return PagedResponse.of(
                logs.getContent().stream().map(this::toResponse).toList(),
                page, size, logs.getTotalElements()
        );
    }

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

    @Transactional
    public AuditLog recordAudit(UUID userId, String actionType, String entityType, UUID entityId,
                               String beforeValue, String afterValue, String ipAddress, String userAgent) {
        Instant now = Instant.now();
        String safeBefore = toJsonSafe(beforeValue);
        String safeAfter = toJsonSafe(afterValue);

        String rawData = String.format("%s:%s:%s:%s:%s:%s",
                now.toString(),
                userId != null ? userId.toString() : "anonymous",
                actionType,
                entityType,
                entityId != null ? entityId.toString() : "none",
                safeAfter != null ? safeAfter : "");
        String hash = computeSha256(rawData);

        AuditLog logEntry = AuditLog.builder()
                .timestamp(now)
                .userId(userId)
                .actionType(actionType)
                .entityType(entityType)
                .entityId(entityId != null ? entityId : UUID.randomUUID())
                .beforeValue(safeBefore)
                .afterValue(safeAfter)
                .ipAddress(ipAddress != null ? ipAddress : "127.0.0.1")
                .userAgent(userAgent != null ? userAgent : "BSUTH Clinical App")
                .integrityHash(hash)
                .build();

        AuditLog saved = auditLogRepository.save(logEntry);
        log.info("Recorded immutable audit entry: id={}, action={}, entityType={}, entityId={}",
                saved.getId(), actionType, entityType, entityId);
        return saved;
    }

    private String toJsonSafe(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        String trimmed = value.trim();
        if ((trimmed.startsWith("{") && trimmed.endsWith("}")) || (trimmed.startsWith("[") && trimmed.endsWith("]"))) {
            return trimmed;
        }
        return "{\"value\":\"" + trimmed.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r") + "\"}";
    }

    private String computeSha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(encodedhash);
        } catch (Exception e) {
            return "HASH_UNAVAILABLE";
        }
    }

    private AuditLogResponse toResponse(AuditLog logItem) {
        String username = "Clinical User";
        if (logItem.getUserId() != null) {
            username = userRepository.findById(logItem.getUserId())
                    .map(User::getUsername)
                    .orElse("user_" + logItem.getUserId().toString().substring(0, 8));
        }

        String justification = extractJustification(logItem.getAfterValue());

        return new AuditLogResponse(
                logItem.getId(),
                logItem.getTimestamp(),
                logItem.getUserId(),
                username,
                logItem.getActionType(),
                logItem.getActionType(),
                logItem.getEntityType(),
                logItem.getEntityId(),
                logItem.getBeforeValue(),
                logItem.getAfterValue(),
                justification,
                logItem.getIpAddress(),
                logItem.getTimestamp()
        );
    }

    private String extractJustification(String afterValue) {
        if (afterValue == null) return null;
        if (afterValue.startsWith("{\"value\":\"") && afterValue.endsWith("\"}")) {
            return afterValue.substring(10, afterValue.length() - 2)
                    .replace("\\\"", "\"").replace("\\\\", "\\").replace("\\n", "\n").replace("\\r", "\r");
        }
        return afterValue;
    }
}
