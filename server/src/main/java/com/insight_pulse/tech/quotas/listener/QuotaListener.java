package com.insight_pulse.tech.quotas.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.auth.event.UserRegisterEvent;
import com.insight_pulse.tech.quotas.service.QuotaService;
import com.insight_pulse.tech.user.domain.User;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class QuotaListener {
    private final QuotaService quotaService;

    @EventListener
    public void handleUserRegisterEvent(UserRegisterEvent event) {
        User user = event.getUser();
        quotaService.initFreeQuota(user);
    }
}
