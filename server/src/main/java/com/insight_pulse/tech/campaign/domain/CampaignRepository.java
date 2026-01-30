package com.insight_pulse.tech.campaign.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.insight_pulse.tech.campaign.domain.interfaces.MonthlyGrowth;
import com.insight_pulse.tech.submission.dto.SubmissionChart;
public interface CampaignRepository extends JpaRepository<Campaign, String> {
    
    Page<Campaign> findAllByUserId(int userId, Pageable pageable);

    Optional<Campaign> findByIdAndUserId(String id, int userId);

    @Modifying(flushAutomatically = true)
    @Query("UPDATE Campaign c SET c.totalSubmissions = c.totalSubmissions + 1 WHERE c.id = :id")
    void incrementTotalSubmissions(@Param("id") String id);

    @Query("SELECT SUM(c.totalSubmissions) FROM Campaign c WHERE c.user.id = :userId")
    Integer getSumSubmissionByUserId(int userId);

    Integer countByUserIdAndStatus(int userId, CampaignStatus status);

    // Tỷ lệ >= 8 của các submission trong tất cả các campaign của user
    @Query("""
        SELECT 
            COUNT(CASE WHEN s.score >= 8 THEN 1 END) * 100.0
            / NULLIF(COUNT(s.score), 0)
        FROM Submission s
        WHERE s.campaign.user.id = :userId
    """)
    Double calculateHightQualityRatio(@Param("userId") int userId);

    /* 
        Biểu đồ submission 7 ngày qua
        đầu tiên là đếm số ngày từ hiện tại trừ đi created của submission
        sau đó nhóm theo số ngày và đếm số submission theo mỗi nhóm
        Nó sẽ trả về một danh sách các mảng đối tượng, mỗi mảng có hai phần tử:
        - Đầu: Số ngày ( String )
        - Cuối: Số lượng submission ( Long )
        Vậy return type của method này sẽ là 1 list các object
    */ 
    @Query(value ="""
            SELECT 
                TO_CHAR(d.date, 'DD/MM/YYYY') as timePoint,
                COUNT(c.id) as value
            FROM 
                generate_series(CURRENT_DATE - INTERVAL '6 days', CURRENT_DATE, '1 day') AS d(date)
                LEFT JOIN 
                submissions s ON TO_CHAR(s.submitted_at, 'DD/MM/YYYY') = TO_CHAR(d.date, 'DD/MM/YYYY')
                LEFT JOIN
                campaigns c ON s.campaigns_id = c.id AND c.owner_id = :userId
                GROUP BY 
                d.date
                ORDER BY 
                d.date ASC;
            """, nativeQuery = true)
    List<SubmissionChart> getSubmissionChartData(@Param("userId") int userId);
        
    @Query(value = """
            SELECT COUNT(CASE WHEN s.submitted_at >= DATE_TRUNC('month', CURRENT_DATE) THEN 1 END) AS currentMonth, 
            COUNT(CASE WHEN s.submitted_at >= DATE_TRUNC('month', CURRENT_DATE - INTERVAL '1 month') AND s.submitted_at < (CURRENT_DATE - INTERVAL '1 month' + INTERVAL '1 day') THEN 1 END) as lastMonth
            FROM submissions s
            JOIN campaigns c ON s.campaigns_id = c.id
            WHERE c.owner_id = :userId
            AND s.is_deleted = false AND s.deleted_at IS NULL
            """, nativeQuery = true)
    MonthlyGrowth countSubmissionGrowth(@Param("userId") int userid);
}
