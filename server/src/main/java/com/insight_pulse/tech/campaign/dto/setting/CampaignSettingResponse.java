package com.insight_pulse.tech.campaign.dto.setting;

import java.util.List;

import com.insight_pulse.tech.automation.dto.AutomationResponse;

public record CampaignSettingResponse(
    List<AutomationResponse> automations
) {
    
}
