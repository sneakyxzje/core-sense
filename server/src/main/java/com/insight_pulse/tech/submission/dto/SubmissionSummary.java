package com.insight_pulse.tech.submission.dto;

import java.time.LocalDateTime;

public record SubmissionSummary(
    String campaignName,
    LocalDateTime submittedAt
) {
    
}
