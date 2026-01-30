package com.insight_pulse.tech.submission.domain;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;

public interface SubmissionRepository extends JpaRepository<Submission, String>, JpaSpecificationExecutor<Submission> {
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

    Optional<Submission> findByIdAndCampaign_User_Id(String submissionId, int userId);


    @Query(value = """
                SELECT s.* FROM submissions s
                JOIN campaigns c ON s.campaigns_id = c.id
                WHERE deleted_at IS NOT NULL
                AND c.owner_id = :userId
            """, 
            countQuery = 
            """
                SELECT count(*) FROM submissions s
                JOIN campaigns c ON s.campaigns_id = c.id
                WHERE deleted_at IS NOT NULL
                AND c.owner_id = :userId
            """,
            nativeQuery = true)
    Page<Submission> findAllArchive(@Param("userId") int userId, Pageable pageable);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
        UPDATE submissions s
        SET is_deleted = true, 
            deleted_at = :now
        FROM campaigns c
        WHERE s.campaigns_id = c.id
        AND s.id = :submissionId
        AND c.owner_id = :userId
        """, nativeQuery = true)
    int archive(
        @Param("submissionId") String submissionId, 
        @Param("userId") int userId, 
        @Param("now") LocalDateTime now
    );

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
        UPDATE submissions s
        SET is_deleted = true, 
            deleted_at = NOW()
        WHERE s.id IN :ids
        """, nativeQuery = true)
    int archiveAll(@Param("ids") List<String> ids);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            UPDATE submissions s 
            SET is_deleted = false,
            deleted_at = null
        FROM campaigns c WHERE s.campaigns_id = c.id
        AND s.id = :submissionId AND c.owner_id = :userId
            """, nativeQuery = true)
    int restore(@Param("submissionId") String submissionId, @Param("userId") int userId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = """
            DELETE FROM submissions s USING campaigns c
            WHERE s.campaigns_id = c.id AND s.id = :submissionId AND c.owner_id = :userId AND deleted_at IS NOT NULL
            """, nativeQuery = true)
    int hardDelete(@Param("submissionId") String submissionId, @Param("userId") int userId);
}
