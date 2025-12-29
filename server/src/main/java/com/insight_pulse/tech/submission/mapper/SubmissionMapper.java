package com.insight_pulse.tech.submission.mapper;

import org.springframework.stereotype.Component;

import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.dto.SubmissionDetailResponse;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;

@Component
public class SubmissionMapper {
    

    public SubmissionResponse toResponse(Submission s) {
        return new SubmissionResponse(
            s.getId(),
            s.getFullname(),
            s.getAiAssessment(),
            s.getAnswers(),
            s.getScore(),
            s.getSubmittedAt(),
            s.getCurrentStage() != null ? s.getCurrentStage().getId() : null
        );
    }

    public SubmissionDetailResponse toDetailResponse(Submission s, String userPrompts) {
        return new SubmissionDetailResponse(
            userPrompts,
            s.getId(),
            s.getAiAssessment(),
            s.getAnswers(),
            s.getScore(),
            s.getSubmittedAt(),
            s.getSchemaSnapshot()
        );
    }
}
