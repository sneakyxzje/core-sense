package com.insight_pulse.tech.interview.dto;

import java.time.LocalDateTime;

public record InterviewDTO(
    String id,
    String candidateName,
    String candidateEmail,
    String campaignName,
    LocalDateTime scheduleAt,
    String location,
    String notes
) {
    
}
