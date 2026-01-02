package com.insight_pulse.tech.notification.dto;

public record NotificationRequest(
    String title,
    String message,
    int userId,
    String type,
    String link
) {
    
}
