package com.insight_pulse.tech.submission.dto;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

public record SubmissionFilter(
    String search,
    Double minScore,
    Double maxScore,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
) {
    
}
