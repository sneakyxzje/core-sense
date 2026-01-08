package com.insight_pulse.tech.automation.dto;

import com.insight_pulse.tech.automation.domain.AutomationEnum;

public record AutomationSummary(
    AutomationEnum eventCode,
    String fromStageId,
    String toStageId,
    boolean isActive
) {
    
}
