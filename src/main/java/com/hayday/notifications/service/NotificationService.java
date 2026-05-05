package com.hayday.notifications.service;

import com.hayday.notifications.model.CreateNotificationRequest;
import com.hayday.notifications.model.NotificationDocument;
import com.hayday.notifications.model.NotificationItem;
import com.hayday.notifications.model.NotificationListResponse;
import com.hayday.notifications.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private static final int DEFAULT_LIMIT = 25;

    private final NotificationRepository repository;

    public NotificationService(NotificationRepository repository) {
        this.repository = repository;
    }

    public NotificationListResponse listNotifications(String userId) {
        Pageable pageable = PageRequest.of(0, DEFAULT_LIMIT);
        Page<NotificationDocument> page = repository.findByUser_idOrderByCreated_atDesc(userId, pageable);

        List<NotificationItem> items = page.getContent().stream()
                .map(this::toItem)
                .toList();

        long unreadCount = repository.countByUser_idAndStatus(userId, "unread");
        long attentionCount = repository.countByUser_idAndCategory(userId, "attention");

        return new NotificationListResponse(
                true,
                items,
                new NotificationListResponse.Meta(page.getTotalElements(), unreadCount, attentionCount, DEFAULT_LIMIT)
        );
    }

    public NotificationItem createNotification(CreateNotificationRequest request) {
        // Check for dedup key if provided
        if (request.dedup_key() != null) {
            Optional<NotificationDocument> existing = repository.findByDedup_key(request.dedup_key());
            if (existing.isPresent()) {
                logger.info("Notification with dedup_key {} already exists", request.dedup_key());
                return toItem(existing.get());
            }
        }

        NotificationDocument doc = new NotificationDocument(
                request.user_id(),
                request.category(),
                request.level(),
                request.title(),
                request.message(),
                request.action_url()
        );

        if (request.animal_id() != null) {
            doc.setAnimal_id(request.animal_id());
        }
        if (request.metadata() != null) {
            doc.setMetadata(request.metadata());
        }
        if (request.dedup_key() != null) {
            doc.setDedup_key(request.dedup_key());
        }

        doc.setId(UUID.randomUUID().toString());
        NotificationDocument saved = repository.save(doc);
        logger.info("Created notification {} for user {}", saved.getId(), request.user_id());

        return toItem(saved);
    }

    public NotificationItem markRead(String id) {
        NotificationDocument doc = findRequired(id);
        doc.setStatus("read");
        doc.setRead_at(LocalDateTime.now());
        NotificationDocument saved = repository.save(doc);
        logger.info("Marked notification {} as read", id);
        return toItem(saved);
    }

    public void markAllRead(String userId) {
        List<NotificationDocument> unread = repository.findByUser_idAndStatusOrderByCreated_atDesc(userId, "unread");
        LocalDateTime now = LocalDateTime.now();
        unread.forEach(doc -> {
            doc.setStatus("read");
            doc.setRead_at(now);
        });
        repository.saveAll(unread);
        logger.info("Marked all {} notifications as read for user {}", unread.size(), userId);
    }

    public void deleteNotification(String id) {
        repository.deleteById(id);
        logger.info("Deleted notification {}", id);
    }

    public void resolveAttentionNotification(String id) {
        NotificationDocument doc = findRequired(id);
        doc.setStatus("resolved");
        doc.setResolved_at(LocalDateTime.now());
        repository.save(doc);
        logger.info("Resolved attention notification {}", id);
    }

    private NotificationDocument findRequired(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + id));
    }

    private NotificationItem toItem(NotificationDocument doc) {
        return new NotificationItem(
                doc.getId(),
                doc.getUser_id(),
                doc.getAnimal_id(),
                doc.getCategory(),
                doc.getLevel(),
                doc.getTitle(),
                doc.getMessage(),
                doc.getAction_url(),
                doc.getMetadata(),
                doc.getStatus(),
                doc.getCreated_at(),
                doc.getRead_at(),
                doc.getResolved_at()
        );
    }
}
