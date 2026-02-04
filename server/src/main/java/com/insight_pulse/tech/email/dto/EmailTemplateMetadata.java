package com.insight_pulse.tech.email.dto;

public record EmailTemplateMetadata(
    String id, String fileName, String displayName, String description, String category, String defaultBody
) {

}
