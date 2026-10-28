package com.hospital.resource.audit;

import com.hospital.resource.audit.dto.AuditLogResponse;
import com.hospital.resource.audit.entity.AuditLog;
import com.hospital.resource.audit.repository.AuditLogRepository;
import com.hospital.resource.audit.service.AuditApplicationService;
import com.hospital.resource.auth.entity.User;
import com.hospital.resource.auth.repository.UserRepository;
import com.hospital.resource.common.dto.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditApplicationServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;
    @Mock
    private UserRepository userRepository;

    private AuditApplicationService service;

    @BeforeEach
    void setUp() {
        service = new AuditApplicationService(auditLogRepository, userRepository);
    }

    @Test
    @DisplayName("getAllAuditLogs returns paginated audit records with resolved username")
    void getAllAuditLogs_returnsPaginatedResponseWithUsername() {
        UUID userId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .username("dr_gbande")
                .fullName("Dr. Terver Gbande")
                .build();

        AuditLog log = AuditLog.builder()
                .id(UUID.randomUUID())
                .timestamp(Instant.now())
                .userId(userId)
                .actionType("RECOMMENDATION_ACCEPT")
                .entityType("BED")
                .entityId(entityId)
                .beforeValue("AVAILABLE")
                .afterValue("OCCUPIED")
                .ipAddress("192.168.1.10")
                .integrityHash("abc123hash")
                .build();

        when(auditLogRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(log), PageRequest.of(0, 10), 1));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        PagedResponse<AuditLogResponse> response = service.getAllAuditLogs(0, 10, null, null);

        assertThat(response).isNotNull();
        assertThat(response.content()).hasSize(1);
        AuditLogResponse item = response.content().get(0);
        assertThat(item.username()).isEqualTo("dr_gbande");
        assertThat(item.action()).isEqualTo("RECOMMENDATION_ACCEPT");
        assertThat(item.entityType()).isEqualTo("BED");
        assertThat(item.entityId()).isEqualTo(entityId);
    }

    @Test
    @DisplayName("recordAudit creates immutable log entry with computed SHA-256 integrity hash")
    void recordAudit_createsLogEntryWithIntegrityHash() {
        UUID userId = UUID.randomUUID();
        UUID entityId = UUID.randomUUID();

        when(auditLogRepository.save(any(AuditLog.class))).thenAnswer(inv -> {
            AuditLog l = inv.getArgument(0);
            l.setId(UUID.randomUUID());
            return l;
        });

        AuditLog created = service.recordAudit(
                userId,
                "RECOMMENDATION_OVERRIDE",
                "BED",
                entityId,
                "AVAILABLE",
                "Clinical CMO override for hemorrhagic fever patient",
                "192.168.1.45",
                "BSUTH Web"
        );

        assertThat(created).isNotNull();
        assertThat(created.getActionType()).isEqualTo("RECOMMENDATION_OVERRIDE");
        assertThat(created.getIntegrityHash()).isNotNull().isNotBlank();
        assertThat(created.getIntegrityHash()).hasSize(64); // SHA-256 is 64 hex characters
        verify(auditLogRepository).save(any(AuditLog.class));
    }
}
