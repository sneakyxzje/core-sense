package com.insight_pulse.tech.gemini.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.gemini.dto.GeminiResponse;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.submission.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/gemini")
public class GeminiController {

    private final SubmissionService submissionService;

    @PostMapping("/{id}/analyze") 
    public ResponseEntity<GeminiResponse> analyze(@PathVariable String id, @RequestBody Map<String, String> body) {
        String prompt = body.getOrDefault("userPrompts", "Mặc định...");
        GeminiResponse result = submissionService.analyzeAndSave(id, prompt);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/compare")
    public ResponseEntity<GeminiResponse> compare(@RequestBody List<SubmissionResponse> request) {
        GeminiResponse result = submissionService.compare(request); 
        return ResponseEntity.ok(result);
    }
}