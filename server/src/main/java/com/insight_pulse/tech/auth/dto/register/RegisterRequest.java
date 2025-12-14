package com.insight_pulse.tech.auth.dto.register;

public record RegisterRequest(
    String fullname,
    String email,
    String password    
) {    
}
