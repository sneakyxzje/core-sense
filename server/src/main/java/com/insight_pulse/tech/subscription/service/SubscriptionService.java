package com.insight_pulse.tech.subscription.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.subscription.domain.Subscription;
import com.insight_pulse.tech.subscription.domain.SubscriptionRepository;
import com.insight_pulse.tech.subscription.dto.SubscriptionResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    
    private final SubscriptionRepository subscriptionRepository;


    public List<SubscriptionResponse> getAllSubscription() {
        List<Subscription> subscriptions = subscriptionRepository.findAll();

        return subscriptions.stream().map(s -> new SubscriptionResponse(
            s.getId(),
            s.getName(),
            s.getDescription(),
            s.getAiLimit(),
            s.getPrice()
        )).toList();
    }
}
