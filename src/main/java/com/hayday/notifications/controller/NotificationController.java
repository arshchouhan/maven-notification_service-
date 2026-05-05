package com.hayday.notifications.controller;

import com.hayday.notifications.model.ApiResponse;
import com.hayday.notifications.model.CreateNotificationRequest;
import com.hayday.notifications.model.NotificationItem;
import com.hayday.notifications.model.NotificationListResponse;
import com.hayday.notifications.service.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<NotificationListResponse> listNotifications(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        // For testing, default to user-1 if not provided
        if (userId == null || userId.isEmpty()) {
            userId = "user-1";
        }
        return ResponseEntity.ok(notificationService.listNotifications(userId));
    }

    @PostMapping
    public ResponseEntity<?> createNotification(@RequestBody CreateNotificationRequest request) {
        try {
            if (request.user_id() == null || request.user_id().isEmpty()) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("user_id is required")
                );
            }
            if (request.category() == null) {
                return ResponseEntity.badRequest().body(
                        ApiResponse.error("category is required")
                );
            }
            NotificationItem item = notificationService.createNotification(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(item));
        } catch (Exception e) {
            logger.error("Error creating notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ApiResponse.error(e.getMessage())
            );
        }
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markRead(@PathVariable String id) {
        try {
            NotificationItem updated = notificationService.markRead(id);
            return ResponseEntity.ok(ApiResponse.ok(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/read-all")
    public ResponseEntity<?> markAllRead(
            @RequestHeader(value = "X-User-Id", required = false) String userId
    ) {
        // For testing, default to user-1 if not provided
        if (userId == null || userId.isEmpty()) {
            userId = "user-1";
        }
        notificationService.markAllRead(userId);
        return ResponseEntity.ok(ApiResponse.ok("All notifications marked as read"));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolveNotification(@PathVariable String id) {
        try {
            notificationService.resolveAttentionNotification(id);
            return ResponseEntity.ok(ApiResponse.ok("Notification resolved"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNotification(@PathVariable String id) {
        try {
            notificationService.deleteNotification(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.error("Error deleting notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
