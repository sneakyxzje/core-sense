package com.insight_pulse.tech.notification.dto;

public record NotificationResponse(
    String title,
    String message,
    String type,
    String link
) {
    
}
