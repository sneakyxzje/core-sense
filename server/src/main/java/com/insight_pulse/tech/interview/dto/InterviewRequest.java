package com.insight_pulse.tech.interview.dto;

public record InterviewRequest(
    String submissionId,
    String schedule,
    String location,
    String notes
) {
    
}
