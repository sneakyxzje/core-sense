package com.insight_pulse.tech.notification.dto;

import java.time.LocalDateTime;

public record NotificationResponse(
    long id,
    String title,
    String message,
    String type,
    String link,
    boolean isRead,
    LocalDateTime createdAt
) {
    
}
