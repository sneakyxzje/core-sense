package com.insight_pulse.tech.subscription.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubscriptionRepository extends JpaRepository<Subscription, String> {
    
}
