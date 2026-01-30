package com.insight_pulse.tech.submission.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.domain.CampaignStageRepository;
import com.insight_pulse.tech.exception.except.ResourceNotFoundException;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;
import com.insight_pulse.tech.submission.dto.SubmissionBulkRequest;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SubmissionBulkService {

    private final SubmissionRepository repository;
    private final CurrentUserProvider currentUserProvider;
    private final CampaignStageRepository campaignStageRepository;

    private List<Submission> validateOwnership(int userId, String campaignId, List<String> submissionIds) {
        List<Submission> submissions = repository.findAllById(submissionIds);

        boolean allValid = submissions.stream()
        .allMatch(s -> s.getCampaign().getId().equals(campaignId) && s.getCampaign().getUser().getId() == userId);

        if(!allValid) {
            throw new RuntimeException("Some submission its not belong to your campaign");
        }
        return submissions;

    }

    @Transactional
    public void executeBulkAction(String campaignId, SubmissionBulkRequest request) {
       int userId = currentUserProvider.getCurrentUserId();
       
       List<Submission> submissions = validateOwnership(userId, campaignId, request.submissionIds());

       switch (request.action()) {
        case MOVE_TO_STAGE:
            moveToStage(submissions, request.targetStageId());
            break;
        case ARCHIVE:
            bulkArchive(submissions);
            break;
        default: 
            throw new IllegalArgumentException("Unsupported bulk action type");
       }
    }

    private void moveToStage(List<Submission> submissions, String targetStageId) {
        CampaignStage targetStage = campaignStageRepository.findById(targetStageId)
        .orElseThrow(() -> new ResourceNotFoundException("Stage not found!"));
        submissions.forEach(submission -> {
            submission.setCurrentStage(targetStage);
        });
        repository.saveAll(submissions);
    }

    private void bulkArchive(List<Submission> submissions) {
        List<String> ids = submissions.stream().map(Submission::getId).toList();
        repository.archiveAll(ids);       
    }
}
