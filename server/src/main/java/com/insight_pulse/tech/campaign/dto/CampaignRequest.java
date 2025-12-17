package com.insight_pulse.tech.campaign.dto;

import java.util.List;

import com.insight_pulse.tech.campaign.domain.FormQuestion;

public record CampaignRequest(
    String name,
    String description,
    String aiSystemPrompt,
    List<FormQuestion> formSchema
) {
    
}
