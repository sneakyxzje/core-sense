package com.insight_pulse.tech.quotas.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.quotas.domain.Quota;
import com.insight_pulse.tech.quotas.domain.QuotaRepository;
import com.insight_pulse.tech.quotas.exception.QuotaExceededException;
import com.insight_pulse.tech.subscription.domain.Subscription;
import com.insight_pulse.tech.subscription.domain.SubscriptionRepository;
import com.insight_pulse.tech.user.domain.User;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuotaService {
    private final QuotaRepository quotaRepository;

    private final SubscriptionRepository subscriptionRepository;
    public void validateQuota(int userId) {
        Quota quota = quotaRepository.findByUserId(userId).orElseThrow(() -> new QuotaExceededException("Quota not found!"));

        if(quota.getUsedCount() >= quota.getSubscription().getAiLimit()) {
            throw new QuotaExceededException("You have run out of free evaluations");
        }
    }

    public void incrementUsage(int userId) {
        quotaRepository.incrementUsedCount(userId);
    }

    @Transactional
    public void initFreeQuota(User user) {
        Subscription subscription = subscriptionRepository.findById("FREE")
        .orElseThrow(() -> new RuntimeException("Subscription not found"));

        Quota quota = new Quota();
        quota.setUser(user);
        quota.setSubscription(subscription);
        quota.setUsedCount(0);
        quota.setUpdatedAt(LocalDateTime.now());
        quotaRepository.save(quota);
    }
}
