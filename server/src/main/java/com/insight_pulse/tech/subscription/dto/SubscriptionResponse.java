package com.insight_pulse.tech.subscription.dto;

import java.math.BigDecimal;

public record SubscriptionResponse(
    String id,
    String name,
    String description,
    int aiLimit,
    BigDecimal price
) {
    
}
