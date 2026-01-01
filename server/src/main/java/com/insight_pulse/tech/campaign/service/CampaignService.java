package com.insight_pulse.tech.campaign.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.domain.CampaignStageRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStageRequest;
import com.insight_pulse.tech.campaign.dto.CampaignStageResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStats;
import com.insight_pulse.tech.campaign.dto.UpdateCampaignRequest;
import com.insight_pulse.tech.campaign.dto.UpdateStageNameRequest;
import com.insight_pulse.tech.campaign.mapper.CampaignMapper;
import com.insight_pulse.tech.exception.ResourceNotFoundException;
import com.insight_pulse.tech.security.context.CurrentUserProvider;

import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import jakarta.persistence.EntityNotFoundException;
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


    private Campaign getOwnedCampaign(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        return campaignRepository.findByIdAndUserId(campaignId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found or access denied"));
    }

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

        CampaignStage campaignStage = CampaignStage.create("Cột mới", 0, savedCampaign);
        campaignStageRepository.save(campaignStage);

        return campaignMapper.toResponse(savedCampaign);
    }

    public CampaignStageResponse createStage(String campaignId, CampaignStageRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));

        Integer maxPosition = campaignStageRepository.findMaxPositionByCampaignId(campaignId);
        int nextPosition = (maxPosition == null) ? 0 : maxPosition + 1;
        CampaignStage campaignStage = new CampaignStage();
        campaignStage.setCampaign(campaign);
        campaignStage.setPosition(nextPosition);
        campaignStage.setStageName(request.stageName());
        campaignStageRepository.save(campaignStage);
        return new CampaignStageResponse(
            campaignStage.getId(),
            request.stageName(),
            campaign.getId(),
            nextPosition
        );
    }

    public List<CampaignStageResponse> getAllStage(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        List<CampaignStage> campaign = campaignStageRepository.findAllByCampaignIdAndCampaignUserId(campaignId, userId);

        return campaign.stream().map((s) -> new CampaignStageResponse(
            s.getId(),
            s.getStageName(),
            s.getCampaign().getId(),
            s.getPosition()
        )).toList();
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
    public CampaignStageResponse updateStageName(String stageId, UpdateStageNameRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        CampaignStage campaignStage = campaignStageRepository.findById(stageId)
        .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy cột này"));
        if (campaignStage.getCampaign().getUser().getId() != userId) {
            throw new RuntimeException("Bạn không có quyền sửa cột này");
        }
        campaignStage.setStageName(request.stageName());
        return new CampaignStageResponse(
            campaignStage.getId(),
            campaignStage.getStageName(),
            campaignStage.getCampaign().getId(),
            campaignStage.getPosition()
        );
    }
}
