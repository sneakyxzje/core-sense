package com.insight_pulse.tech.campaign.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import com.insight_pulse.tech.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name ="campaigns")
public class Campaign {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name="campaigns_name", nullable = false, length=100)
    private String name;

    @Column(name="campaigns_description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name="campaigns_status")
    @Enumerated(EnumType.STRING)
    private CampaignStatus status = CampaignStatus.NEW;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<FormQuestion> formSchema;

    @Column(columnDefinition = "TEXT") 
    private String aiSystemPrompt;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User user;

    @CreationTimestamp
    @Column(updatable = false) 
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    private long totalSubmissions;

    public static Campaign create(String name, String description, String prompt, List<FormQuestion> formSchema, User user) {
        Campaign campaign = new Campaign();
        campaign.setName(name);
        campaign.setDescription(description);
        campaign.setAiSystemPrompt(prompt);
        campaign.setFormSchema(formSchema);
        campaign.setUser(user);
        campaign.setStatus(CampaignStatus.ACTIVE); 
        return campaign;
    }

    public void update(String name, String description, String prompt, List<FormQuestion> formSchema) {
        this.name = name;
        this.description = description;
        this.aiSystemPrompt = prompt;
        this.formSchema = formSchema;
    }

    public void toggleStatus(Boolean enabled) {
        if (Boolean.TRUE.equals(enabled)) {
            this.status = CampaignStatus.ACTIVE;
        } else {
            this.status = CampaignStatus.INACTIVE;
        }
    }
    
}
