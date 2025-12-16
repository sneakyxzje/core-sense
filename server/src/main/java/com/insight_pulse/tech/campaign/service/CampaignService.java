package com.insight_pulse.tech.campaign.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.dto.CreateCampaignRequest;
import com.insight_pulse.tech.campaign.dto.CreateCampaignResponse;
import com.insight_pulse.tech.campaign.dto.GetCampaignByIdResponse;
import com.insight_pulse.tech.campaign.dto.GetCampaignResponse;
import com.insight_pulse.tech.security.principal.UserDetailsImpl;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignService {
    
    private final CampaignRepository campaignRepository;
    private final UserRepository userRepository;
    public CreateCampaignResponse createCampaign(CreateCampaignRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetailsImpl.getId();
        User user = userRepository.getReferenceById(userId);
        Campaign campaign = new Campaign();
        campaign.setName(request.name());
        campaign.setDescription(request.description());
        campaign.setAiSystemPrompt(request.aiSystemPrompt());
        campaign.setFormSchema(request.formSchema());
        campaign.setUser(user);
        Campaign saved = campaignRepository.save(campaign);
        CampaignStatus status = saved.getStatus();

        return new CreateCampaignResponse(
            saved.getId(),
            saved.getName(),
            saved.getDescription(),
            status,
            saved.getCreatedAt()
        );
    }

    public List<GetCampaignResponse> getCampaigns() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetailsImpl.getId();
        List<Campaign> campaigns = campaignRepository.findAllByUserId(userId);
        return campaigns.stream()
        .map(campaign -> new GetCampaignResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getCreatedAt()
        ))
        .toList();
    }

    public GetCampaignByIdResponse getCampaignById(String campaignId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetailsImpl = (UserDetailsImpl) authentication.getPrincipal();
        int userId = userDetailsImpl.getId(); 
        User user = new User();
        user.setId(userId);
        Campaign campaign = campaignRepository.findByIdAndUser(campaignId, user)
        .orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        return new GetCampaignByIdResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getFormSchema(),
            campaign.getAiSystemPrompt(),
            campaign.getCreatedAt(),
            campaign.getUpdatedAt()
        );
    }
}
