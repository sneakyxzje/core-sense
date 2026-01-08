package com.insight_pulse.tech.campaign.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.automation.domain.Automation;
import com.insight_pulse.tech.automation.domain.AutomationRepository;
import com.insight_pulse.tech.automation.dto.AutomationSummary;
import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.domain.CampaignStageRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStats;
import com.insight_pulse.tech.campaign.dto.UpdateCampaignRequest;
import com.insight_pulse.tech.campaign.dto.stage.CreateStageRequest;
import com.insight_pulse.tech.campaign.dto.stage.StageResponse;
import com.insight_pulse.tech.campaign.dto.stage.UpdateStageNameRequest;
import com.insight_pulse.tech.campaign.mapper.CampaignMapper;
import com.insight_pulse.tech.exception.ResourceNotFoundException;
import com.insight_pulse.tech.security.context.CurrentUserProvider;

import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {

    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignMapper campaignMapper;
    private final CampaignStageRepository campaignStageRepository;
    private final AutomationRepository automationRepository;
    private static final String DEFAULT_STAGE_NAME = "Cột mới";
    private Campaign getOwnedCampaign(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        return campaignRepository.findByIdAndUserId(campaignId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found or access denied"));
    }

    @Transactional
    public CampaignResponse createCampaign(CampaignRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        Campaign campaign = Campaign.create(
            request.name(), 
            request.description(), 
            request.aiSystemPrompt(), 
            request.formSchema(), 
            user
        );
        Campaign savedCampaign = campaignRepository.save(campaign);

        CampaignStage campaignStage = CampaignStage.create(DEFAULT_STAGE_NAME, 0, savedCampaign);
        campaignStageRepository.save(campaignStage);

        return campaignMapper.toResponse(savedCampaign);
    }

    @Transactional
    public StageResponse createStage(String campaignId, CreateStageRequest request) {
        
        Campaign campaign = getOwnedCampaign(campaignId); 

        Integer maxPosition = campaignStageRepository.findMaxPositionByCampaignId(campaignId);
        int nextPosition = (maxPosition == null) ? 0 : maxPosition + 1;
        CampaignStage campaignStage = CampaignStage.create(request.stageName(), nextPosition, campaign);
        campaignStageRepository.save(campaignStage);
        return campaignMapper.toStageResponse(campaignStage);
    }

    public List<StageResponse> getAllStage(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        List<CampaignStage> stages = campaignStageRepository.findAllByCampaignIdAndCampaignUserId(campaignId, userId);
        return stages.stream().map(campaignMapper::toStageResponse).toList();
    }

    public CampaignStats getCampaignInfo() {
        int userId = currentUserProvider.getCurrentUserId();
        Double ratio = campaignRepository.calculateHightQualityRatio(userId);
        return new CampaignStats(
            campaignRepository.getSumSubmissionByUserId(userId),
            campaignRepository.countByUserIdAndStatus(userId, CampaignStatus.ACTIVE),
            ratio != null ? ratio : 0.0
        );
    }
    public Page<CampaignResponse> getCampaigns(Pageable pageable) {
        int userId = currentUserProvider.getCurrentUserId();
        Page<Campaign> campaigns = campaignRepository.findAllByUserId(userId, pageable);
        return campaigns.map(campaignMapper::toResponse);
    }

    public CampaignDetailResponse getCampaignById(String campaignId) {
        Campaign campaign = getOwnedCampaign(campaignId);
        return campaignMapper.toDetailResponse(campaign);
    }

    @Transactional
    public void toggleCampaignStatus(String campaignId, Boolean enabled) {        
        Campaign campaign = getOwnedCampaign(campaignId);
        campaign.toggleStatus(enabled); 
        campaignRepository.save(campaign); 
    }

    @Transactional
    public CampaignDetailResponse updateCampaign(String campaignId, UpdateCampaignRequest request) {
        Campaign campaign = getOwnedCampaign(campaignId);
        campaign.update(
            request.name(), 
            request.description(), 
            request.aiSystemPrompt(), 
            request.formSchema()
        );
        return campaignMapper.toDetailResponse(campaign);
    }

    @Transactional
    public StageResponse updateStageName(String stageId, UpdateStageNameRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        CampaignStage campaignStage = campaignStageRepository.findByIdAndCampaignUserId(stageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Stage not found or access denied"));
        campaignStage.setStageName(request.stageName());
        return campaignMapper.toStageResponse(campaignStage);
    }

    public List<AutomationSummary> getCampaignAutomations(String campaignId) {
        Campaign campaign = getOwnedCampaign(campaignId);
        List<Automation> automations = automationRepository.findAllByCampaignId(campaign.getId());
        return automations.stream().map((s) -> new AutomationSummary(
            s.getEventCode(), 
            (s.getFromStage() != null) ? s.getFromStage().getId() : null, 
            (s.getToStage() != null) ? s.getToStage().getId() : null,   
            s.isActive())
        ).toList();
    }
}
