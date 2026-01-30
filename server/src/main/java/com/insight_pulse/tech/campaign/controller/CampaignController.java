package com.insight_pulse.tech.campaign.controller;

import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.automation.dto.AutomationSummary;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStats;
import com.insight_pulse.tech.campaign.dto.UpdateCampaignRequest;
import com.insight_pulse.tech.campaign.dto.setting.CampaignSettingRequest;
import com.insight_pulse.tech.campaign.dto.stage.CreateStageRequest;
import com.insight_pulse.tech.campaign.dto.stage.StageResponse;
import com.insight_pulse.tech.campaign.dto.stage.UpdateStageColumnRequest;
import com.insight_pulse.tech.campaign.dto.stage.UpdateStageNameRequest;
import com.insight_pulse.tech.campaign.dto.submission.CampaignWithSubmissionsResponse;
import com.insight_pulse.tech.campaign.service.CampaignService;
import com.insight_pulse.tech.campaign.service.CampaignSettingsService;
import com.insight_pulse.tech.submission.dto.DeleteStageRequest;
import com.insight_pulse.tech.submission.dto.SubmissionBulkRequest;
import com.insight_pulse.tech.submission.dto.SubmissionChart;
import com.insight_pulse.tech.submission.dto.SubmissionDetailResponse;
import com.insight_pulse.tech.submission.dto.SubmissionFilter;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.submission.service.SubmissionBulkService;
import com.insight_pulse.tech.submission.service.SubmissionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/campaigns")
@Slf4j
public class CampaignController {

    
    private final CampaignSettingsService campaignSettingsService;
    private final CampaignService campaignService;
    private final SubmissionService submissionService;
    private final SubmissionBulkService submissionBulkService;
    // --- CRUD ---
    @PostMapping
    public ResponseEntity<CampaignResponse> createCampaign(@Valid @RequestBody CampaignRequest request) {
        CampaignResponse response = campaignService.createCampaign(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<CampaignResponse>> getCampaigns(Pageable pageable) {
        return ResponseEntity.ok(campaignService.getCampaigns(pageable));
    }
    
    @GetMapping("/{campaignId}")
    public ResponseEntity<CampaignDetailResponse> getCampaignById(@PathVariable String campaignId) {
        return ResponseEntity.ok(campaignService.getCampaignById(campaignId));
    }
    
    @PutMapping("/{campaignId}")
    public ResponseEntity<CampaignDetailResponse> updateCampaign(@PathVariable String campaignId, @RequestBody UpdateCampaignRequest request) {
        return ResponseEntity.ok(campaignService.updateCampaign(campaignId, request));
    }
    
    @PatchMapping("/{campaignId}/status")
    public ResponseEntity<?> toggleCampaignStatus(@PathVariable String campaignId, @RequestBody Map<String, Boolean> body) {
        boolean enabled = body.get("enabled");
        campaignService.toggleCampaignStatus(campaignId, enabled);
        return ResponseEntity.ok().build();
    }
    
    // --- Stats / Dashboard ---
    @GetMapping("/stats/info")
    public ResponseEntity<CampaignStats> getCampaignsInfo() {
        CampaignStats info = campaignService.getCampaignInfo();
        return ResponseEntity.ok(info);
    }
    
    @GetMapping("/stats/submission-chart")
    public ResponseEntity<List<SubmissionChart>> getSubmissionChart() {
        List<SubmissionChart> chartData = submissionService.getSubmissionChart();
        return ResponseEntity.ok(chartData);
    }

    // --- Submission in campaign ---

    @GetMapping("/{campaignId}/submissions")
    public ResponseEntity<CampaignWithSubmissionsResponse> findSubmissionsByCampaign(@PathVariable String campaignId, SubmissionFilter filter, Pageable pageable) {
        return ResponseEntity.ok(submissionService.findSubmissionByCampaign(campaignId, filter, pageable));
    }

    @GetMapping("/{campaignId}/submissions/{submissionId}")
    public ResponseEntity<SubmissionDetailResponse> getSubmissionDetailByCampaign(@PathVariable String campaignId, @PathVariable String submissionId) {
        return ResponseEntity.ok(submissionService.getSubmissionDetailByCampaign(campaignId, submissionId));
    }
    

    @PatchMapping("/{campaignId}/submissions/{submissionId}/stage")
    public ResponseEntity<SubmissionResponse> updateSubmissionStage(@PathVariable String submissionId, @RequestBody UpdateStageColumnRequest request) {
        return ResponseEntity.ok(submissionService.updateStageColumn(submissionId, request));
    }

    @PostMapping("/{campaignId}/submissions:bulk-move")
    public ResponseEntity<Void> bulkMoveSubmission(@PathVariable String campaignId, @RequestBody SubmissionBulkRequest request) {
        submissionBulkService.executeBulkAction(campaignId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{campaignId}/submissions:bulk-archive")
    public ResponseEntity<Void> bulkArchiveSubmission(@PathVariable String campaignId, @RequestBody SubmissionBulkRequest request) {
        submissionBulkService.executeBulkAction(campaignId, request);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{campaignId}/stages")
    public ResponseEntity<StageResponse> createStage(@PathVariable String campaignId, @RequestBody CreateStageRequest request) {
        return ResponseEntity.ok(campaignService.createStage(campaignId, request));
    }
    
    @GetMapping("/{campaignId}/stages")
    public ResponseEntity<List<StageResponse>> getAllStage(@PathVariable String campaignId) {
        return ResponseEntity.ok(campaignService.getAllStage(campaignId));
    }

    @PatchMapping("/{campaignId}/stages/{stageId}")
    public ResponseEntity<StageResponse> updateStageName(@PathVariable String stageId, @RequestBody UpdateStageNameRequest request) {
        return ResponseEntity.ok(campaignService.updateStageName(stageId, request));
    }

    @DeleteMapping("/{campaignId}/stages/delete")
    public ResponseEntity<?> deleteStage(@RequestBody DeleteStageRequest request) {
        submissionService.deleteStageColumn(request);
        return ResponseEntity.noContent().build(); 
    }

    @PutMapping("/{campaignId}/settings")
    public ResponseEntity<Void> updateSettings(
        @PathVariable String campaignId, 
        @RequestBody CampaignSettingRequest request
    ) {
        log.info("Request: {}", request);
        campaignSettingsService.saveAllSettings(campaignId, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{campaignId}/automations")
    public ResponseEntity<List<AutomationSummary>> getCampaignAutomations(@PathVariable String campaignId) {
        return ResponseEntity.ok(campaignService.getCampaignAutomations(campaignId));
    }
}
