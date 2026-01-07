package com.insight_pulse.tech.automation.event;

import com.insight_pulse.tech.automation.domain.AutomationEnum;

import lombok.Getter;

@Getter
public class AutomationTriggerEvent {
    private final Integer userId;
    private final String campaignId;
    private final String submissionId;
    private final AutomationEnum eventCode;

    public AutomationTriggerEvent(Integer userId,String campaignId, String submissionId, AutomationEnum eventCode) {
        this.userId = userId;
        this.campaignId = campaignId;
        this.submissionId = submissionId;
        this.eventCode = eventCode;
    }
}
