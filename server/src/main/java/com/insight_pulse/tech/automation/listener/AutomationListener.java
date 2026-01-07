package com.insight_pulse.tech.automation.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.automation.event.AutomationTriggerEvent;
import com.insight_pulse.tech.automation.service.AutomationService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AutomationListener {
    private final AutomationService automationService;

    @EventListener
    public void onAutomationTrigger(AutomationTriggerEvent event) {
        automationService.executeAutomation(event.getUserId(), event.getSubmissionId(), event.getEventCode());
    }
}
