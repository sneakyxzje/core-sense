package com.insight_pulse.tech.interview.dto;

public record InterviewResponse(
    String interviewId,
    String schedule,
    String location,
    String notes
) {
    
}
