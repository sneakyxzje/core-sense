package com.insight_pulse.tech.campaign.dto;

import org.springframework.data.domain.Page;

import com.insight_pulse.tech.submission.dto.SubmissionResponse;

public record CampaignWithSubmissionsResponse(
    CampaignDetailResponse campaign,
    Page<SubmissionResponse> submissions
) {}