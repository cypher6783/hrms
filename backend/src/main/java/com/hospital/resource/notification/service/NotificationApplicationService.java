package com.hospital.resource.notification.service;

import com.hospital.resource.common.exception.ResourceNotFoundException;
import com.hospital.resource.notification.dto.NotificationResponse;
import com.hospital.resource.notification.entity.Notification;
import com.hospital.resource.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationApplicationService {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(NotificationApplicationService.class);

    private final NotificationRepository notificationRepository;

    @Transactional
    public void createNotification(String title, String message, String type, String module,
                                    UUID entityTypeId, UUID entityId, UUID recipientUserId) {
        Notification notification = Notification.builder()
                .title(title)
                .message(message)
                .notificationType(type)
                .sourceModule(module)
                .sourceEntityType(entityTypeId != null ? entityTypeId.toString() : null)
                .sourceEntityId(entityId)
                .recipientUserId(recipientUserId)
                .isRead(false)
                .build();

        notificationRepository.save(notification);
        log.info("Notification created: type={}, recipientUserId={}", type, recipientUserId);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotifications(UUID userId) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUnreadNotifications(UUID userId) {
        return notificationRepository.findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", notificationId.toString()));
        notification.setIsRead(true);
        notification.setReadAt(Instant.now());
        notificationRepository.save(notification);
    }

    @Transactional
    public void markAllAsRead(UUID userId) {
        notificationRepository.markAllAsRead(userId, Instant.now());
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(), notification.getTitle(), notification.getMessage(),
                notification.getNotificationType(), notification.getSourceModule(),
                notification.getIsRead(), notification.getReadAt(), notification.getCreatedAt()
        );
    }
}
