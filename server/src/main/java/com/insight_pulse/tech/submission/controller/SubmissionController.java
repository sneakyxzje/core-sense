package com.insight_pulse.tech.submission.controller;


import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight_pulse.tech.campaign.dto.PublicCampaignResponse;
import com.insight_pulse.tech.submission.dto.DeleteStageRequest;
import com.insight_pulse.tech.submission.dto.SubmissionRequest;
import com.insight_pulse.tech.submission.dto.SubmissionStarRequest;
import com.insight_pulse.tech.submission.dto.SubmissionSummary;
import com.insight_pulse.tech.submission.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submission")
public class SubmissionController {
 
    private final SubmissionService submissionService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    @PostMapping("/{campaignId}")
    public ResponseEntity<String> submitForm(@PathVariable String campaignId, 
        @RequestPart("file") MultipartFile file,
        @RequestPart("data") String dataJson
    ) {
        try {
            SubmissionRequest requestDto = objectMapper.readValue(dataJson, SubmissionRequest.class);
            submissionService.submitForm(campaignId, requestDto, file);
            return ResponseEntity.ok("Submit thành công");
        } catch (JsonProcessingException ex) {
            return ResponseEntity.badRequest().body("JSON type error: " + ex.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi xử lý: " + e.getMessage());
        }
    }

    @GetMapping("/campaign-schema/{campaignId}")
    public ResponseEntity<PublicCampaignResponse> getPublicSchema(@PathVariable String campaignId) {
        return ResponseEntity.ok(submissionService.getPublicSchema(campaignId));
    }

    @GetMapping
    public ResponseEntity<Page<SubmissionSummary>> getSubmissionSummary() {
        return ResponseEntity.ok(submissionService.getSubmissionSummary());
    }

    @PatchMapping("/{submissionId}/star")
    public ResponseEntity<Void> toggleStarred(@PathVariable String submissionId, @RequestBody SubmissionStarRequest request) {
        submissionService.toggleStarredStatus(submissionId, request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("stages/delete")
    public ResponseEntity<?> deleteStage(@RequestBody DeleteStageRequest request) {
        submissionService.deleteStageColumn(request);
        return ResponseEntity.noContent().build(); 
    }
}
