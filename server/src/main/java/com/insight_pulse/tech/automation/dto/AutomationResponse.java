package com.insight_pulse.tech.automation.dto;

public record AutomationResponse(
    String userId,
    String type,   
    String submissionId,    
    String fromStageId,   
    String toStageId,       
    String message
) {
    
}
