package com.insight_pulse.tech.campaign.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignRequest;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.CampaignWithSubmissionsResponse;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {
    
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;
    private final CurrentUserProvider currentUserProvider;
    public CampaignResponse createCampaign(CampaignRequest request) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        Campaign campaign = new Campaign();
        campaign.setName(request.name());
        campaign.setDescription(request.description());
        campaign.setAiSystemPrompt(request.aiSystemPrompt());
        campaign.setFormSchema(request.formSchema());
        campaign.setUser(user);
        Campaign saved = campaignRepository.save(campaign);
        CampaignStatus status = saved.getStatus();

        return new CampaignResponse(
            saved.getId(),
            saved.getName(),
            saved.getDescription(),
            status,
            saved.getCreatedAt()
        );
    }

    public List<CampaignResponse> getCampaigns() {
        int userId = currentUserProvider.getCurrentUserId();
        List<Campaign> campaigns = campaignRepository.findAllByUserId(userId);
        return campaigns.stream()
        .map(campaign -> new CampaignResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getCreatedAt()
        ))
        .toList();
    }

    public CampaignDetailResponse getCampaignById(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = new User();
        user.setId(userId);
        Campaign campaign = campaignRepository.findByIdAndUser(campaignId, user)
        .orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        long totalSubmissions = submissionRepository.countByCampaign_Id(campaignId);
        return new CampaignDetailResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getFormSchema(),
            campaign.getAiSystemPrompt(),
            campaign.getCreatedAt(),
            campaign.getUpdatedAt(),
            totalSubmissions
        );
    }

    public CampaignWithSubmissionsResponse getSubmissionByCampaign(String campaignId) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = new User(); user.setId(userId);
        Campaign campaign = campaignRepository.findByIdAndUser(campaignId, user).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        List<Submission> submissions = submissionRepository.findAllByCampaignId(campaignId);
        long totalSubmissions = submissions.size();
        CampaignDetailResponse campaignResponse = new CampaignDetailResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getFormSchema(),
            campaign.getAiSystemPrompt(),
            campaign.getCreatedAt(),
            campaign.getUpdatedAt(),
            totalSubmissions
        );
        List<SubmissionResponse> submissionResponse = submissions.stream().map(s -> new SubmissionResponse(
            s.getId(),
            s.getAiAssessment(),
            s.getAnswers(),
            s.getScore(),
            s.getSubmittedAt()
        )).toList();
        return new CampaignWithSubmissionsResponse(
            campaignResponse,
            submissionResponse
        );
    }
}
