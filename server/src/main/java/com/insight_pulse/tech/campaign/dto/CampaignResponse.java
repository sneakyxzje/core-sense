package com.insight_pulse.tech.campaign.dto;

import java.time.LocalDateTime;

import com.insight_pulse.tech.campaign.domain.CampaignStatus;

public record CampaignResponse(
    String id,
    String name,
    String description,
    CampaignStatus status,
    LocalDateTime createdAt
) {}