package com.insight_pulse.tech.submission.domain;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SubmissionRepository extends JpaRepository<Submission, String> {
    List<Submission> findAllByCampaignId(String campaignId);

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
    SELECT * 
    FROM submissions s 
    WHERE s.campaigns_id = :campaignId 
      AND CAST(s.answers AS TEXT) ILIKE CONCAT('%', :search, '%')
    """, nativeQuery = true)
    List<Submission> searchSubmission(@Param("campaignId") String campaignId, @Param("search") String search);


    Page<Submission> findByCampaignUserId(int userId, Pageable pageable);

    long countByCampaign_Id(String campaignId);
}
