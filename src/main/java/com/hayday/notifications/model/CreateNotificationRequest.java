package com.hayday.notifications.model;

import java.util.Map;

public record CreateNotificationRequest(
        String user_id,
        String animal_id,
        String category,
        String level,
        String title,
        String message,
        String action_url,
        Map<String, Object> metadata,
        String dedup_key
) {
}
