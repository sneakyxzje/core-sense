package com.insight_pulse.tech.campaign.dto;

import java.util.List;

import com.insight_pulse.tech.submission.dto.SubmissionResponse;

public record CampaignWithSubmissionsResponse(
    CampaignDetailResponse campaign,
    List<SubmissionResponse> submissions
) {}