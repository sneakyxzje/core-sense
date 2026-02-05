package com.insight_pulse.tech.quotas.dto;

import com.insight_pulse.tech.subscription.domain.Subscription;

public record QuotaResponse(
    int usedCount,
    Subscription subscription
) {
    
}
