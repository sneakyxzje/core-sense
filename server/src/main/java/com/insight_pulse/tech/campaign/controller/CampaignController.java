package com.insight_pulse.tech.campaign.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStats;
import com.insight_pulse.tech.campaign.dto.CampaignWithSubmissionsResponse;
import com.insight_pulse.tech.campaign.dto.UpdateCampaignRequest;
import com.insight_pulse.tech.campaign.service.CampaignService;
import com.insight_pulse.tech.submission.dto.SubmissionChart;
import com.insight_pulse.tech.submission.dto.SubmissionDetailResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
public class CampaignController {
    
    private final CampaignService campaignService;

    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@Valid @RequestBody CampaignRequest request) {
        CampaignResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaigns(pageable));
    }

    @GetMapping("/campaign-info")
    public ResponseEntity<CampaignStats> getCampaignsInfo() {
        CampaignStats info = campaignService.getCampaignInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/submission-chart")
    public ResponseEntity<List<SubmissionChart>> getSubmissionChart() {
        List<SubmissionChart> chartData = campaignService.getSubmissionChart();
        return ResponseEntity.ok(chartData);
    }

    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignDetailResponse> getCampaignById(@PathVariable String campaignId) {
        return ResponseEntity.ok(campaignService.getCampaignById(campaignId));
    }

    @GetMapping("/{campaignId}/submissions")
    public ResponseEntity<CampaignWithSubmissionsResponse> findSubmissionsByCampaign(@PathVariable String campaignId, @RequestParam(required=false) String search, Pageable pageable) {
        return ResponseEntity.ok(campaignService.findSubmissionByCampaign(campaignId, search, pageable));
    }

    @GetMapping("/{campaignId}/submissions/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getSubmissionDetailByCampaign(@PathVariable String campaignId, @PathVariable String submissionId) {
        return ResponseEntity.ok(campaignService.getSubmissionDetailByCampaign(campaignId, submissionId));
    }
    
    @PostMapping("/{campaignId}/toggle-status")
    public ResponseEntity<?> toggleCampaignStatus(@PathVariable String campaignId, @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.get("enabled");
        campaignService.toggleCampaignStatus(campaignId, enabled);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{campaignId}")
    public ResponseEntity<CampaignDetailResponse> updateCampaign(@PathVariable String campaignId, @RequestBody UpdateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(campaignId, request));
    }
}
