package com.insight_pulse.tech.automation.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
import com.insight_pulse.tech.email.service.EmailService;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AutomationService {
    private final AutomationRepository automationRepository;
    private final CampaignRepository campaignRepository;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignStageRepository campaignStageRepository;
    private final SubmissionRepository submissionRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    private String mailDemo = "hr@coresense.tech";
    private String subjectDemo = "[INTERVIEW]: Interview schedule";
    public void executeAutomation(Integer userId, String submissionId, AutomationEnum eventCode) {
        Submission submission = submissionRepository.findById(submissionId).orElseThrow(() -> new RuntimeException("Submission not found"));
        String fromStageId = submission.getCurrentStage().getId();
        Optional<Automation> ruleOpt = automationRepository.findByCampaignIdAndEventCodeAndFromStageId(submission.getCampaign().getId(), eventCode, submission.getCurrentStage().getId());
        if(ruleOpt.isPresent() && ruleOpt.get().isActive()) {
            Automation rule = ruleOpt.get();
            switch(eventCode) {
                case AI_FILTER:
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
                    messagingTemplate.convertAndSend(
                        "/topic/" + userId + "/campaign/" + submission.getCampaign().getId(), 
                        response
                    );
                    break;
                case MAIL_AUTO:
                    emailService.sendFlexible(mailDemo, subjectDemo, "Test body",null, null, submissionId);               
                    response = new AutomationResponse(
                        userId.toString(),
                        "AI_MAIL_AUTO",
                        submissionId,
                        fromStageId,
                        rule.getToStage().getId(),
                        "Đang tự động gửi email.. "
                    );
                    messagingTemplate.convertAndSend(
                        "/topic/" + userId + "/campaign/" + submission.getCampaign().getId(), 
                        response
                    );
                    break;
            }
        } 
    }

    @Transactional
    public void updateRules(String campaignId, List<AutomationRequest> request) {
        int userId = currentUserProvider.getCurrentUserId();
        Campaign campaign = campaignRepository.findByIdAndUserId(campaignId, userId)
        .orElseThrow(() -> new RuntimeException("Campaign not found"));
        
        if(request == null) request = Collections.emptyList();

        List<AutomationRequest> validRequests = request.stream()
        .filter(req -> {
            if (req.eventCode() == AutomationEnum.AI_FILTER) {
                return req.toStage() != null && req.fromStage() != null && !req.fromStage().isEmpty();
            } else if (req.eventCode() == AutomationEnum.MAIL_AUTO) {
                return req.toStage() != null;
            }
            return false;
        }).toList();

        boolean hasExisting = automationRepository.existsByCampaignId(campaignId);
        if(validRequests.isEmpty() && !hasExisting) {
            return;
        }

        Set<String> stageIds = new HashSet<>();
        validRequests.forEach(req -> {
            if(req.fromStage() != null) stageIds.add(req.fromStage());
            stageIds.add(req.toStage());
        });

        Map<String, CampaignStage> stageMap = campaignStageRepository.findAllById(stageIds).stream()
        .collect(Collectors.toMap(CampaignStage::getId, Function.identity()));


        automationRepository.deleteByCampaignId(campaignId);
        automationRepository.flush();

        if(!validRequests.isEmpty()) {
            List<Automation> newAutomations = validRequests.stream().map(req -> {
                CampaignStage from;
                CampaignStage to;

                if (req.eventCode() == AutomationEnum.MAIL_AUTO) {
                    from = stageMap.get(req.toStage());
                    to = from; 
                } else {
                    from = stageMap.get(req.fromStage());
                    to = stageMap.get(req.toStage());
                }

                if(to == null) throw new RuntimeException("Target stage not found");

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
}
