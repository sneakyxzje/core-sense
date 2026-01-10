package com.insight_pulse.tech.automation.domain;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignStage;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name="automations", uniqueConstraints = {
    @UniqueConstraint(
        name="uc_campaign_event",
        columnNames = {"campaign_id", "eventCode", "from_stage_id"}
    )
})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Automation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AutomationEnum eventCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_stage_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampaignStage fromStage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_stage_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private CampaignStage toStage;

    @Column(nullable = false)
    private boolean isActive;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Automation create(Campaign campaign, AutomationEnum eventCode, CampaignStage fromStage, CampaignStage toStage, boolean isActive) {
        Automation automation = new Automation();
        automation.setCampaign(campaign);
        automation.setEventCode(eventCode);
        automation.setFromStage(fromStage);
        automation.setToStage(toStage);
        automation.setActive(isActive);
        return automation;
    }
}

