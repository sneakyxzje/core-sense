package com.insight_pulse.tech.email.dto;

public record EmailTemplateDTO(
    String id, String displayName, String description, String category, String content
) {
    
}
