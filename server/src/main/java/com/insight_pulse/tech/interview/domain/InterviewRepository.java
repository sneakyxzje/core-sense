package com.insight_pulse.tech.interview.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findAllByScheduleAtBetweenAndReminderSentFalse(LocalDateTime start, LocalDateTime end);        
}
