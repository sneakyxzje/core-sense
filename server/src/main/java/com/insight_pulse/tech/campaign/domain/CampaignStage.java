package com.insight_pulse.tech.campaign.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="campaign_stages")
public class CampaignStage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String stageName;

    private Integer position;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;
}
