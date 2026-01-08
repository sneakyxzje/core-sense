package com.insight_pulse.tech.campaign.dto.setting;

import java.util.List;

import com.insight_pulse.tech.automation.dto.AutomationRequest;

import lombok.Data;

@Data
public class CampaignSettingRequest {
    private List<AutomationRequest> automations;
}
