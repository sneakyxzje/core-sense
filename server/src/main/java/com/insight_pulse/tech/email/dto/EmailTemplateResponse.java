package com.insight_pulse.tech.email.dto;

public record EmailTemplateResponse(
    String slug,
    String name,
    String subject,
    String description,
    String customBody    
) {
    
}
