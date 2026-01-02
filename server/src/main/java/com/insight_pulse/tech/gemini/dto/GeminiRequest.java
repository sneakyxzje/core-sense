package com.insight_pulse.tech.gemini.dto;


import java.util.List;
import java.util.Map;

import com.insight_pulse.tech.campaign.domain.FormQuestion;

public record GeminiRequest(
    List<FormQuestion> formSchema,
    Map<String, Object> answer,
    String userPrompt,
    String resume
) {
}