package com.insight_pulse.tech.campaign.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CampaignStageRepository extends JpaRepository<CampaignStage, String> {
    @Query("SELECT MAX(s.position) FROM CampaignStage s WHERE s.campaign.id = :campaignId")
    Integer findMaxPositionByCampaignId(String campaignId);

    List<CampaignStage> findAllByCampaignIdAndCampaignUserId(String campaignId, int userId);   

    CampaignStage findByCampaignIdAndCampaignUserId(String campaignId, int userId);

    @Modifying
    @Query("""
            UPDATE CampaignStage s 
            SET s.position = s.position - 1
            WHERE s.campaign.id = :campaignId
            AND s.position > :deletedPosition
            """)
    void shiftPositions(@Param("campaignId") String campaignId, @Param("deletedPosition") int deletedPosition);

    Optional<CampaignStage> findByCampaignIdAndPosition(String campaignId, int position);

    Optional<CampaignStage> findByIdAndCampaignUserId(String id, int userId);
}
