package com.insight_pulse.tech.campaign.dto;

public record CampaignStageResponse(
    String id,
    String stageName,
    String campaignId,
    Integer position
) {
}
