package com.insight_pulse.tech.submission.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import com.insight_pulse.tech.campaign.domain.Campaign;
import com.insight_pulse.tech.campaign.domain.CampaignStage;
import com.insight_pulse.tech.campaign.domain.FormQuestion;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="submissions")
public class Submission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "fullname", nullable = false, columnDefinition = "varchar(255) default 'N/A'")
    private String fullname;

    @Column(name = "email", nullable = false, columnDefinition = "varchar(255) default 'N/A'")
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="campaigns_id", nullable = false)
    private Campaign campaign;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> answers;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<FormQuestion> schemaSnapshot;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> aiAssessment;

    private Double score;

    @CreationTimestamp
    private LocalDateTime submittedAt;

    @ManyToOne
    @JoinColumn(name ="current_stage_id")
    private CampaignStage currentStage;

    @Column(name = "is_starred", nullable = false, columnDefinition = "boolean default false")
    private boolean starred = false; 

    @Column(name = "cv_url", length = 500)
    private String cvUrl;
}
