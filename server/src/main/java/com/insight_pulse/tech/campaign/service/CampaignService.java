package com.insight_pulse.tech.campaign.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignStats;
import com.insight_pulse.tech.campaign.dto.CampaignWithSubmissionsResponse;
import com.insight_pulse.tech.campaign.dto.UpdateCampaignRequest;
import com.insight_pulse.tech.campaign.mapper.CampaignMapper;
import com.insight_pulse.tech.exception.ResourceNotFoundException;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.submission.dto.SubmissionChart;
import com.insight_pulse.tech.submission.dto.SubmissionDetailResponse;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.submission.mapper.SubmissionMapper;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;
import com.insight_pulse.tech.utils.PageableUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {

    
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignMapper campaignMapper;
    private final SubmissionMapper submissionMapper;

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
        return campaignMapper.toResponse(campaignRepository.save(campaign));
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

    public List<SubmissionChart> getSubmissionChart() {
        int userId = currentUserProvider.getCurrentUserId();
        return campaignRepository.getSubmissionChartData(userId);
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

    public SubmissionDetailResponse getSubmissionDetailByCampaign(String campaignId, String submissionId) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        String userPrompts = campaign.getAiSystemPrompt();
        Submission submission = submissionRepository.findSubmissionDetail(userId, campaignId, submissionId);
        return submissionMapper.toDetailResponse(submission, userPrompts);
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


    public CampaignWithSubmissionsResponse findSubmissionByCampaign(String campaignId, String search, Pageable pageable) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        Page<Submission> submissions;

        if(StringUtils.hasText(search)) {
            Pageable nativePageable = PageableUtils.convertToNativePageable(pageable);
            submissions = submissionRepository.searchSubmission(campaignId, search.trim(), nativePageable);
        }
        else {
            submissions = submissionRepository.findAllByCampaignId(campaignId, pageable);
        }
        
        CampaignDetailResponse campaignResponse = campaignMapper.toDetailResponse(campaign);
        Page<SubmissionResponse> submissionResponse = submissions.map(submissionMapper::toResponse);
        return new CampaignWithSubmissionsResponse(
            campaignResponse,
            submissionResponse
        );
    }
}
