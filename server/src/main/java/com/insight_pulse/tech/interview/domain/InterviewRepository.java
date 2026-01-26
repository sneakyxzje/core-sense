package com.insight_pulse.tech.interview.domain;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface InterviewRepository extends JpaRepository<Interview, String> {
    List<Interview> findAllByScheduleAtBetweenAndReminderSentFalse(LocalDateTime start, LocalDateTime end);        

    @EntityGraph(attributePaths = {"submission", "submission.campaign"})
    Page<Interview> findAllByUserId(int userId, Pageable pageable);

    Interview findBySubmission_Id(String submissionId);

    @Query("""
            SELECT i from Interview i
            JOIN FETCH i.submission s
            JOIN FETCH s.campaign c 
            WHERE i.scheduleAt >= :start
            AND i.scheduleAt < :end
            AND i.user.id = :userId
            ORDER BY i.scheduleAt ASC
            """)
    List<Interview> next7DaysInterviews(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end, @Param("userId") int userId); 
}
