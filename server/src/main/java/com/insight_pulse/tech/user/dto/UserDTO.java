package com.insight_pulse.tech.user.dto;


public record UserDTO(
    int id,
    String fullname,
    String email,
    int usedCount,
    String planName,
    int aiLimit,
    String subscriptionId
) {
    
}
