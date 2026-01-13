package com.insight_pulse.tech.submission.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.insight_pulse.tech.campaign.domain.FormQuestion;

public record SubmissionDetailResponse(
    String prompt,
    String id,
    Map<String, Object> aiAssessment,
    Map<String, Object> answer,
    Double score,
    LocalDateTime submittedAt,
    List<FormQuestion> snapshotSchema,
    String cvUrl,
    String fullName,
    String email
) {}