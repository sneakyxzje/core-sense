package com.insight_pulse.tech.submission.dto;

import java.util.List;

import com.insight_pulse.tech.submission.domain.enums.BulkActionType;

public record SubmissionBulkRequest(
    List<String> submissionIds,
    BulkActionType action,
    String targetStageId
) {

}
