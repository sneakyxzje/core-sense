package com.insight_pulse.tech.campaign.dto;

import java.util.List;

import com.insight_pulse.tech.campaign.domain.FormQuestion;

public record UpdateCampaignRequest(
    String name,
    String description,
    List<FormQuestion> formSchema,
    String aiSystemPrompt
) {
}
