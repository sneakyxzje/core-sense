package com.insight_pulse.tech.automation.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AutomationRepository extends JpaRepository<Automation, Long>{
    Optional<Automation> findByCampaignIdAndEventCode(String campaignId, AutomationEnum eventCode);    

    Optional<Automation> findByCampaignIdAndEventCodeAndFromStageId(String campaignId, AutomationEnum eventCode, String fromStageId);    

    void deleteByCampaignId(String campaignId);

    List<Automation> findAllByCampaignId(String campaignId);

    boolean existsByCampaignId(String campaignId);
}
