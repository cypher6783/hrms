package com.hospital.resource.notification.repository;

import com.hospital.resource.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(UUID recipientUserId);

    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientUserId);

    long countByRecipientUserIdAndIsReadFalse(UUID recipientUserId);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :now WHERE n.recipientUserId = :userId AND n.isRead = false")
    void markAllAsRead(@org.springframework.data.repository.query.Param("userId") UUID userId, @org.springframework.data.repository.query.Param("now") java.time.Instant now);
}
