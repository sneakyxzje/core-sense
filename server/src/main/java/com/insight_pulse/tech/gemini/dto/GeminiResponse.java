package com.insight_pulse.tech.gemini.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GeminiResponse(
    @JsonProperty("aiAssesment") String aiAssesment,
    @JsonProperty("summary") String summary,
    @JsonProperty("positive") String positive,
    @JsonProperty("negative") String negative,
    @JsonProperty("score") double score,
    @JsonProperty("highlights") List<Highlight> highlights
) {
    public record Highlight(
        String text,
        String type,    
        String comment
    ) {}
}