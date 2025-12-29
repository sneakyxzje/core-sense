package com.insight_pulse.tech.submission.service;


import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStatus;
import com.insight_pulse.tech.campaign.domain.FormQuestion;
import com.insight_pulse.tech.campaign.dto.CampaignDetailResponse;
import com.insight_pulse.tech.campaign.dto.CampaignWithSubmissionsResponse;
import com.insight_pulse.tech.campaign.dto.PublicCampaignResponse;
import com.insight_pulse.tech.campaign.mapper.CampaignMapper;
import com.insight_pulse.tech.gemini.dto.GeminiRequest;
import com.insight_pulse.tech.gemini.dto.GeminiResponse;
import com.insight_pulse.tech.gemini.service.GeminiService;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.submission.dto.SubmissionChart;
import com.insight_pulse.tech.submission.dto.SubmissionDetailResponse;
import com.insight_pulse.tech.submission.dto.SubmissionEvent;
import com.insight_pulse.tech.submission.dto.SubmissionRequest;
import com.insight_pulse.tech.submission.dto.SubmissionResponse;
import com.insight_pulse.tech.submission.dto.SubmissionSummary;
import com.insight_pulse.tech.submission.mapper.SubmissionMapper;
import com.insight_pulse.tech.utils.PageableUtils;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionService {

    private final CampaignRepository campaignRepository;
    private final SubmissionRepository submissionRepository;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpMessagingTemplate messagingTemplate;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignMapper campaignMapper;
    private final SubmissionMapper submissionMapper;


    @Transactional
    public void submitForm(String campaignId, SubmissionRequest request) {
        Campaign campaign = campaignRepository.findById(campaignId).orElseThrow(() -> new RuntimeException("Campaign not found"));
        if (campaign.getStatus() != CampaignStatus.ACTIVE) {
            throw new RuntimeException("Campaign này đã đóng hoặc chưa kích hoạt!");
        }
        List<FormQuestion> schemaSnapshot = campaign.getFormSchema();
        Submission submission = new Submission();
        submission.setFullname(request.answers().getSysName());
        submission.setEmail(request.answers().getSysEmail());
        submission.setCampaign(campaign);
        submission.setAnswers(request.answers().getAllAnswers());
        submission.setSchemaSnapshot(schemaSnapshot);
        submissionRepository.save(submission);
        campaignRepository.incrementTotalSubmissions(campaignId);

        SubmissionEvent event = new SubmissionEvent(
            campaign.getName(),
            submission.getId(),
            "Một phản hồi mới đã được gửi đến chiến dịch " + campaign.getName(),
            campaign.getTotalSubmissions(),
            submission.getSubmittedAt()
        );
        messagingTemplate.convertAndSend("/topic/submissions", event);
        System.out.println("New submission sent to /topic/submissions");
    }

    public PublicCampaignResponse getPublicSchema(String campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
        .orElseThrow(() -> new RuntimeException("Campaign not found"));

        return new PublicCampaignResponse(
            campaign.getName(),
            campaign.getFormSchema()
        );
    }

    

    public Page<SubmissionSummary> getSubmissionSummary() {
        int userId = currentUserProvider.getCurrentUserId();
        Pageable top5 = PageRequest.of(0, 5, Sort.by("submittedAt").descending());
        Page<Submission> submissions = submissionRepository.findByCampaignUserId(userId, top5);
        return submissions.map(s -> new SubmissionSummary(
            s.getCampaign().getName(),
            s.getSubmittedAt()
        ));
    }

    public CampaignWithSubmissionsResponse findSubmissionByCampaign(String campaignId, String search, Pageable pageable) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        Page<Submission> submissions;

        if(StringUtils.hasText(search)) {
            Pageable nativePageable = PageableUtils.convertToNativePageable(pageable);
            submissions = submissionRepository.searchSubmission(campaignId, search.trim(), nativePageable);
        }
        else {
            submissions = submissionRepository.findAllByCampaignId(campaignId, pageable);
        }
        
        CampaignDetailResponse campaignResponse = campaignMapper.toDetailResponse(campaign);
        Page<SubmissionResponse> submissionResponse = submissions.map(submissionMapper::toResponse);
        return new CampaignWithSubmissionsResponse(
            campaignResponse,
            submissionResponse
        );
    }

    public List<SubmissionChart> getSubmissionChart() {
        int userId = currentUserProvider.getCurrentUserId();
        return campaignRepository.getSubmissionChartData(userId);
    }

    public SubmissionDetailResponse getSubmissionDetailByCampaign(String campaignId, String submissionId) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId).orElseThrow(() -> new RuntimeException("Campaign not found or permission denied"));
        String userPrompts = campaign.getAiSystemPrompt();
        Submission submission = submissionRepository.findSubmissionDetail(userId, campaignId, submissionId);
        return submissionMapper.toDetailResponse(submission, userPrompts);
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

    public GeminiResponse compare(List<SubmissionResponse> request) {
        List<String> ids = request.stream()
                .map(r -> (r.id())) 
                .toList();
        List<Submission> submissions = submissionRepository.findAllById(ids);
        if (submissions.size() < 2) {
            throw new IllegalArgumentException("Cần ít nhất 2 phản hồi để so sánh");
        }

        List<GeminiRequest> geminiRequest = submissions.stream()
        .map((s) -> new GeminiRequest(
            s.getSchemaSnapshot(),
            s.getAnswers(), ""
        )).toList();

        GeminiResponse aiResult = geminiService.compare(geminiRequest);
        return aiResult;
    }
}
