package com.insight_pulse.tech.notification.dto;

import org.springframework.data.domain.Page;

public record NotificationSummary(
    Page<NotificationResponse> notifications,
    long unreadCount
) {
    
}
