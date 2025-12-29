package com.insight_pulse.tech.campaign.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CampaignStageRepository extends JpaRepository<CampaignStage, String> {
    @Query("SELECT MAX(s.position) FROM CampaignStage s WHERE s.campaign.id = :campaignId")
    Integer findMaxPositionByCampaignId(String campaignId);

    List<CampaignStage> findAllByCampaignIdAndCampaignUserId(String campaignId, int userId);   

    CampaignStage findByCampaignIdAndCampaignUserId(String campaignId, int userId);
}
