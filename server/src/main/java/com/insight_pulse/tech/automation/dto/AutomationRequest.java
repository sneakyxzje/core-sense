package com.insight_pulse.tech.automation.dto;

import com.insight_pulse.tech.automation.domain.AutomationEnum;

public record AutomationRequest(
    String campaignId,
    AutomationEnum eventCode, 
    String fromStage,
    String toStage,
    boolean status
) {
    
}
