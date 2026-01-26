package com.insight_pulse.tech.interview.domain;

import java.time.LocalDateTime;

import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.user.domain.User;

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
@Table(name="interviews")
public class Interview {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="itv_submission_id", nullable = false)
    private Submission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name ="schedule_at", nullable = false)
    private LocalDateTime scheduleAt;

    @Column(name ="reminder_sent")
    private boolean reminderSent = false;

    @Column(name = "location")
    private String location;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public static Interview create(Submission submission, LocalDateTime scheduleAt, String location, String notes, User user) {
        if(submission == null) throw new IllegalArgumentException("Submission not found");
        if(scheduleAt == null) throw new IllegalArgumentException("Schedule is null!");
        if(scheduleAt.isBefore(LocalDateTime.now())) throw new IllegalArgumentException("Schedule cannot be in the past");

        Interview interview = new Interview();
        interview.setSubmission(submission);
        interview.setScheduleAt(scheduleAt);
        interview.setLocation(location);
        interview.setNotes(notes);
        interview.setUser(user);
        return interview;
    }
}
