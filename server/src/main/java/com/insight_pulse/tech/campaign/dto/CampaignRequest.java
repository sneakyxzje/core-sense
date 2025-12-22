package com.insight_pulse.tech.campaign.dto;

import java.util.List;

import com.insight_pulse.tech.campaign.domain.FormQuestion;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record CampaignRequest(
    @NotBlank(message="Campaign name cant not be null!") String name,
    @NotBlank(message="Description cant not be null") String description,
    String aiSystemPrompt,
    @NotEmpty(message="Schema cant not be null") List<FormQuestion> formSchema
) {
    
}
