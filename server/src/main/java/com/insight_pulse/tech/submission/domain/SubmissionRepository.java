package com.insight_pulse.tech.submission.domain;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
    Page<Submission> findAllByCampaignId(String campaignId, Pageable pageable);

    @Query("""
        SELECT s
        FROM Submission s
        WHERE s.campaign.id = :campaignId
        AND s.campaign.user.id = :userId
    """)
    List<Submission> findByCampaignAndUser(
        @Param("campaignId") String campaignId,
        @Param("userId") int userId
    );

    @Query("""
            SELECT s 
            FROM Submission s WHERE s.campaign.id = :campaignId AND s.campaign.user.id = :userId AND s.id = :submissionId
            """)
    Submission findSubmissionDetail(
        @Param("userId") int userId,
        @Param("campaignId") String campaignId,
        @Param("submissionId") String submissionId
    );

    @Query(value = """
        SELECT * FROM submissions s 
        WHERE s.campaigns_id = :campaignId 
        AND (
            LOWER(CAST(s.fullname AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) 
        )
        """, 
        countQuery = """
        SELECT count(*) FROM submissions s 
        WHERE s.campaigns_id = :campaignId 
        AND (
            LOWER(CAST(s.answers AS TEXT)) LIKE LOWER(CONCAT('%', :search, '%')) 
        )
        """,
        nativeQuery = true)
    Page<Submission> searchSubmission(@Param("campaignId") String campaignId, @Param("search") String search, Pageable pageable);


    Page<Submission> findByCampaignUserId(int userId, Pageable pageable);

    long countByCampaign_Id(String campaignId);

    long countByCurrentStageId(String stageId);

    @Modifying
    @Query("UPDATE Submission s SET s.currentStage.id = :targetStageId WHERE s.currentStage.id = :oldStageId")
    void migrateSubmissions(String oldStageId, String targetStageId);

    @Modifying
    @Query("""
            UPDATE Submission s SET s.starred = :status WHERE s.id = :submissionId
            """)
    void updateStarredStatus(String submissionId, boolean status);

    @Query("""
            SELECT s.cvUrl FROM Submission s WHERE s.id = :submissionId
            """)
    String findCVUrlBySubmissionId(String submissionId);
}
