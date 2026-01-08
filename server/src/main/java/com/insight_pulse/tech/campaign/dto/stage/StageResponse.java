package com.insight_pulse.tech.campaign.dto.stage;

public record StageResponse(
    String id,
    String stageName,
    String campaignId,
    Integer position
) {
}
