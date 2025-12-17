package com.insight_pulse.tech.campaign.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.domain.FormQuestion;

public record CampaignDetailResponse(
    String id,
    String name,
    String description,
    CampaignStatus status,
    List<FormQuestion> formSchema,
    String aiSystemPrompt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    long totalSubmissions
) {}