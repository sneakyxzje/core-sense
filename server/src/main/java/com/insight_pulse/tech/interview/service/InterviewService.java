package com.insight_pulse.tech.interview.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.interview.domain.Interview;
import com.insight_pulse.tech.interview.domain.InterviewRepository;
import com.insight_pulse.tech.interview.dto.InterviewDTO;
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

        Interview checkInterview = interviewRepository.findBySubmission_Id(request.submissionId());
        if (checkInterview != null) {
            throw  new RuntimeException("Interview for this submission already exists");
        }

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

    public Page<InterviewDTO> getAllInterview(Pageable pageable) {
        int userId = currentUserProvider.getCurrentUserId();
        Page<Interview> interviews = interviewRepository.findAllByUserId(userId, pageable);

        return interviews.map((i) -> new InterviewDTO(
            i.getId(),
            i.getSubmission().getFullname(),
            i.getSubmission().getEmail(),
            i.getSubmission().getCampaign().getName(),
            i.getScheduleAt(),
            i.getLocation(),
            i.getNotes()
        ));
    }

    public List<InterviewDTO> getNext7DaysInterviews() {
        int userId = currentUserProvider.getCurrentUserId();
        LocalDateTime start = LocalDateTime.now(); 
        LocalDateTime end = start.plusDays(6);
        List<Interview> interviews = interviewRepository.next7DaysInterviews(start, end, userId);
        return interviews.stream().map((i) -> new InterviewDTO(
            i.getId(),
            i.getSubmission().getFullname(),
            i.getSubmission().getEmail(),
            i.getSubmission().getCampaign().getName(),
            i.getScheduleAt(),
            i.getLocation(),
            i.getNotes()
        )).toList();
    }
}
