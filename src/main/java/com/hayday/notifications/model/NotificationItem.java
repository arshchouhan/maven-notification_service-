package com.hayday.notifications.model;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationItem(
        String id,
        String user_id,
        String animal_id,
        String category,
        String level,
        String title,
        String message,
        String action_url,
        Map<String, Object> metadata,
        String status,
        LocalDateTime created_at,
        LocalDateTime read_at,
        LocalDateTime resolved_at
) {
}
