package com.hospital.resource.notification.controller;

import com.hospital.resource.auth.security.SecurityUtils;
import com.hospital.resource.common.dto.ApiResponse;
import com.hospital.resource.notification.dto.NotificationResponse;
import com.hospital.resource.notification.service.NotificationApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationApplicationService notificationService;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(notificationService.getNotifications(userId));
    }

    @GetMapping("/unread")
    public ApiResponse<List<NotificationResponse>> getUnreadNotifications() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/unread/count")
    public ApiResponse<Long> getUnreadCount() {
        UUID userId = SecurityUtils.getCurrentUserId();
        return ApiResponse.success(notificationService.getUnreadCount(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Void> markAsRead(@PathVariable UUID id) {
        UUID userId = SecurityUtils.getCurrentUserId();
        notificationService.markAsRead(id, userId);
        return ApiResponse.success("Marked as read", null);
    }

    @PostMapping("/read-all")
    public ApiResponse<Void> markAllAsRead() {
        UUID userId = SecurityUtils.getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ApiResponse.success("All marked as read", null);
    }
}
