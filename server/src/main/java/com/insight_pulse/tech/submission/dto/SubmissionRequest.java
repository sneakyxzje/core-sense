package com.insight_pulse.tech.submission.dto;

import java.util.Map;

public record SubmissionRequest(
    Map<String, Object> answers
) {
    
}
