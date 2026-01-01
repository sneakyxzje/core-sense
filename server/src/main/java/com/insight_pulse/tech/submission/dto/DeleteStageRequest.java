package com.insight_pulse.tech.submission.dto;

public record DeleteStageRequest(
    String stageToDelete,
    String targetStage
) {
    
}
