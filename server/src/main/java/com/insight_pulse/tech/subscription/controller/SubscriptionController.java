package com.insight_pulse.tech.subscription.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.subscription.dto.SubscriptionResponse;
import com.insight_pulse.tech.subscription.service.SubscriptionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/subscriptions")
public class SubscriptionController {
    private final SubscriptionService subscriptionService;

    @GetMapping 
    public ResponseEntity<List<SubscriptionResponse>> getAllSubscription() {
        return ResponseEntity.ok(subscriptionService.getAllSubscription());
    }
}
