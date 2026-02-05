package com.insight_pulse.tech.quotas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.quotas.dto.QuotaResponse;
import com.insight_pulse.tech.quotas.service.QuotaService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quotas")
public class QuotaController {
    private final QuotaService quotaService;


    @GetMapping
    public ResponseEntity<QuotaResponse> getAllQuota() {
        return ResponseEntity.ok(quotaService.getAllQuota());
    }
}
