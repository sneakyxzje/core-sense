package com.insight_pulse.tech.campaign.service;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.automation.service.AutomationService;
import com.insight_pulse.tech.campaign.dto.setting.CampaignSettingRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CampaignSettingsService {
    private final AutomationService automationService;
    @Transactional
    public void saveAllSettings(String campaignId, CampaignSettingRequest request) {
        automationService.updateRules(campaignId, request.getAutomations());
    }
}
