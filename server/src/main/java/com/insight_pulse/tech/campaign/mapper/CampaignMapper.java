package com.insight_pulse.tech.campaign.mapper;


import org.springframework.stereotype.Component;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignResponse;
import com.insight_pulse.tech.campaign.dto.stage.StageResponse;

@Component
public class CampaignMapper {
    public CampaignResponse toResponse(Campaign campaign) {
        if (campaign == null) return null;
        
        return new CampaignResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getCreatedAt()
        );
    }

    public CampaignDetailResponse toDetailResponse(Campaign campaign) {
        if (campaign == null) return null;
        return new CampaignDetailResponse(
            campaign.getId(),
            campaign.getName(),
            campaign.getDescription(),
            campaign.getStatus(),
            campaign.getFormSchema(),
            campaign.getAiSystemPrompt(),
            campaign.getCreatedAt(),
            campaign.getUpdatedAt(),
            campaign.getTotalSubmissions()
        );
    }

    public StageResponse toStageResponse(CampaignStage stage) {
        return new StageResponse(
            stage.getId(),
            stage.getStageName(),
            stage.getCampaign().getId(),
            stage.getPosition()
        );
    }
}
