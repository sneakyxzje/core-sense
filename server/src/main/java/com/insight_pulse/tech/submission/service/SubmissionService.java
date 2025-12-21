package com.insight_pulse.tech.submission.service;


import java.util.List;
import java.util.Map;


import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.domain.FormQuestion;
import com.insight_pulse.tech.campaign.dto.PublicCampaignResponse;
import com.insight_pulse.tech.gemini.dto.GeminiRequest;
import com.insight_pulse.tech.gemini.dto.GeminiResponse;
import com.insight_pulse.tech.gemini.service.GeminiService;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.submission.dto.SubmissionRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final CampaignRepository campaignRepository;
    private final SubmissionRepository submissionRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Transactional
    public void submitForm(String campaignId, SubmissionRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new RuntimeException("Campaign này đã đóng hoặc chưa kích hoạt!");
        }
        List<FormQuestion> schemaSnapshot = campaign.getFormSchema();
        Submission submission = new Submission();
        submission.setCampaign(campaign);
        submission.setAnswers(request.answers());
        submission.setSchemaSnapshot(schemaSnapshot);
        submissionRepository.save(submission);
        campaignRepository.incrementTotalSubmissions(campaignId);
    }

    public PublicCampaignResponse getPublicSchema(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
        .orElseThrow(() -> new RuntimeException("Campaign not found"));

        return new PublicCampaignResponse(
            campaign.getName(),
            campaign.getFormSchema()
        );
    }

    @Transactional
    public GeminiResponse analyzeAndSave(String submissionId, String userPrompt) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new RuntimeException("Not found"));

        GeminiRequest request = new GeminiRequest(
             submission.getSchemaSnapshot(),
             submission.getAnswers(),
             userPrompt
        );

        GeminiResponse aiResult = geminiService.analyze(request);
        try {
            Map<String, Object> assessmentMap = objectMapper.convertValue(
                aiResult, 
                new TypeReference<Map<String, Object>>() {}
            );
            submission.setAiAssessment(assessmentMap); 
            submission.setScore(aiResult.score());
            
            submissionRepository.save(submission);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aiResult;
    }
}
