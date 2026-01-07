package com.insight_pulse.tech.automation.service;

import java.util.List;
import java.util.Optional;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.insight_pulse.tech.automation.domain.Automation;
import com.insight_pulse.tech.automation.domain.AutomationEnum;
import com.insight_pulse.tech.automation.domain.AutomationRepository;
import com.insight_pulse.tech.automation.dto.AutomationRequest;
import com.insight_pulse.tech.automation.dto.AutomationResponse;
import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignRepository;
import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.domain.CampaignStageRepository;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutomationService {
    private final AutomationRepository automationRepository;
    private final CampaignRepository campaignRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignStageRepository campaignStageRepository;
    private final SubmissionRepository submissionRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public void executeAutomation(Integer userId, String submissionId, AutomationEnum eventCode) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Submission not found"));
        String fromStageId = submission.getCurrentStage().getId();
        Optional<Automation> ruleOpt = automationRepository.findByCampaignIdAndEventCodeAndFromStageId(submission.getCampaign().getId(), eventCode, submission.getCurrentStage().getId());
        if(ruleOpt.isPresent() && ruleOpt.get().isActive()) {
            Automation rule = ruleOpt.get();
            submission.setCurrentStage(rule.getToStage());
            submissionRepository.save(submission);
            AutomationResponse response = new AutomationResponse(
                userId.toString(),
                "AI_AUTOMATION_MOVE",
                submissionId,
                fromStageId,
                rule.getToStage().getId(),
                "AI đã tự động chuyển hồ sơ sang cột " + rule.getToStage().getStageName()
            );
            System.out.println(">>> WebSocket sending to user: " + userId);
            messagingTemplate.convertAndSend(
                "/topic/" + userId + "/campaign/" + submission.getCampaign().getId(), 
                response
            );
        } 
    }

    @Transactional
    public void updateRules(String campaignId, List<AutomationRequest> request) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId)
        .orElseThrow(() -> new RuntimeException("Campaign not found"));
        automationRepository.deleteByCampaignId(campaignId);
        automationRepository.flush();
        List<Automation> newAutomations = request.stream().map(req -> {
                CampaignStage from = null;
                if (req.fromStage() != null && !req.fromStage().isEmpty()) {
                    from = campaignStageRepository.findById(req.fromStage())
                        .orElseThrow(() -> new RuntimeException("From stage not found"));
                }
                
                CampaignStage to = campaignStageRepository.findById(req.toStage())
                    .orElseThrow(() -> new RuntimeException("Target stage not found"));

                return Automation.builder()
                    .campaign(campaign)
                    .eventCode(req.eventCode()) 
                    .fromStage(from)
                    .toStage(to)
                    .isActive(req.status())
                    .build();
            }).toList();
        automationRepository.saveAll(newAutomations);
    }
}
