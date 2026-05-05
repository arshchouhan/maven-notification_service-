package com.hayday.notifications.model;

import java.util.List;

public record NotificationListResponse(
        boolean success,
        List<NotificationItem> data,
        Meta meta
) {
    public record Meta(long total_count, long unread_count, long attention_count, int limit) {
    }
}
