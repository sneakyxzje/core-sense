package com.insight_pulse.tech.submission.controller;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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

import com.insight_pulse.tech.campaign.dto.PublicCampaignResponse;
import com.insight_pulse.tech.submission.dto.SubmissionRequest;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.submission.dto.SubmissionStarRequest;
import com.insight_pulse.tech.submission.dto.SubmissionSummary;
import com.insight_pulse.tech.submission.service.SubmissionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/submissions")
public class SubmissionController {
 
    private final SubmissionService submissionService;
    @PostMapping(value = "/{campaignId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> submitForm(@PathVariable String campaignId, 
        @RequestPart("file") MultipartFile file,
        @RequestPart("data") SubmissionRequest request
    ) {
            submissionService.submitForm(campaignId, request, file);
            return ResponseEntity.ok("Submit thành công");
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

    @PostMapping("/{submissionId}/archive")
    public ResponseEntity<?> archiveSubmission(@PathVariable String submissionId) {
        submissionService.archiveSubmission(submissionId);
        return ResponseEntity.noContent().build();
    } 

    @PostMapping("/{submissionId}/restore")
    public ResponseEntity<?> restoreSubmission(@PathVariable String submissionId) {
        submissionService.restoreSubmission(submissionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{submissionId}/delete")
    public ResponseEntity<?> deleteSubmission(@PathVariable String submissionId) {
        submissionService.deleteSubmission(submissionId);
        return ResponseEntity.ok().build();
    }

    @GetMapping({"/archived"})
    public ResponseEntity<Page<SubmissionResponse>> getAllArchive(Pageable pageable) {
        return ResponseEntity.ok(submissionService.getAllArchive(pageable));
    }
}
