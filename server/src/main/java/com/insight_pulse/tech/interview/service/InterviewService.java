package com.insight_pulse.tech.interview.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.interview.domain.Interview;
import com.insight_pulse.tech.interview.domain.InterviewRepository;
import com.insight_pulse.tech.interview.dto.InterviewRequest;
import com.insight_pulse.tech.interview.dto.InterviewResponse;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final SubmissionRepository submissionRepository;    
    private final InterviewRepository interviewRepository;
    private final CurrentUserProvider currentUserProvider;   
    private final UserRepository userRepository;
    public InterviewResponse createInterview(InterviewRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);

        Submission submission = submissionRepository.findById(request.submissionId())
        .orElseThrow(() -> new RuntimeException("Submission not found"));

        LocalDateTime parseSchedule = LocalDateTime.parse(request.schedule());

        Interview interview = Interview.create(submission, parseSchedule, request.location(), request.notes(), user);
        Interview savedInterview = interviewRepository.save(interview);
        return new InterviewResponse(
            savedInterview.getId(),
            savedInterview.getScheduleAt().toString(),
            savedInterview.getLocation(),
            savedInterview.getNotes()
        );
    }
}
