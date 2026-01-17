package com.insight_pulse.tech.submission.dto;

import java.time.LocalDateTime;
import java.util.Map;


public record SubmissionResponse(
    String id,
    String fullName,
    String email,
    Map<String, Object> aiAssessment,
    Map<String, Object> answer,
    Double score,
    LocalDateTime submittedAt,
    String stageId,
    LocalDateTime deletedAt,
    String campaignName,
    boolean starred
) {}