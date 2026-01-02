package com.insight_pulse.tech.notification.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="title")
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    @Column(name="user_id", nullable = false)
    private int userId;

    private String type;

    private boolean isRead = false; 

    private String link; 

    private LocalDateTime createdAt = LocalDateTime.now(); 

    public static Notification create(String title, String message, int userId, String type, String link) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setUserId(userId);
        notification.setType(type);
        notification.setLink(link);
        return notification;
    }
}
