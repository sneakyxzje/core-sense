package com.insight_pulse.tech.campaign.dto;

public record CampaignStats(
    int submissionThisMonth,
    double differencePercent,
    Integer activeCampaigns,
    Double highQualityRatio
) {
    
}
