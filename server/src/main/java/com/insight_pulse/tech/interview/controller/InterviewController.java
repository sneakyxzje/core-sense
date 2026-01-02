package com.insight_pulse.tech.interview.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.interview.dto.InterviewRequest;
import com.insight_pulse.tech.interview.dto.InterviewResponse;
import com.insight_pulse.tech.interview.service.InterviewService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    @PostMapping
    public ResponseEntity<InterviewResponse> createInterview(@RequestBody InterviewRequest request) {
        System.out.println("Request: " + request);
        InterviewResponse response = interviewService.createInterview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
