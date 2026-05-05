package com.hayday.notifications.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Map;

@Document(collection = "notifications")
public class NotificationDocument {

    @Id
    private String id;
    private String user_id;
    private String animal_id;
    private String category; // activity | attention
    private String level; // info | warning | danger | success
    private String title;
    private String message;
    private String action_url;
    private Map<String, Object> metadata;
    private String status; // unread | read | resolved
    private LocalDateTime created_at;
    private LocalDateTime read_at;
    private LocalDateTime resolved_at;
    private String dedup_key; // for de-duplicating recurring alerts

    // Constructors
    public NotificationDocument() {}

    public NotificationDocument(String user_id, String category, String level, String title, String message, String action_url) {
        this.user_id = user_id;
        this.category = category;
        this.level = level;
        this.title = title;
        this.message = message;
        this.action_url = action_url;
        this.status = "unread";
        this.created_at = LocalDateTime.now();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUser_id() { return user_id; }
    public void setUser_id(String user_id) { this.user_id = user_id; }

    public String getAnimal_id() { return animal_id; }
    public void setAnimal_id(String animal_id) { this.animal_id = animal_id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getAction_url() { return action_url; }
    public void setAction_url(String action_url) { this.action_url = action_url; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreated_at() { return created_at; }
    public void setCreated_at(LocalDateTime created_at) { this.created_at = created_at; }

    public LocalDateTime getRead_at() { return read_at; }
    public void setRead_at(LocalDateTime read_at) { this.read_at = read_at; }

    public LocalDateTime getResolved_at() { return resolved_at; }
    public void setResolved_at(LocalDateTime resolved_at) { this.resolved_at = resolved_at; }

    public String getDedup_key() { return dedup_key; }
    public void setDedup_key(String dedup_key) { this.dedup_key = dedup_key; }
}
