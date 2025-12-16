package com.insight_pulse.tech.campaign.domain;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.insight_pulse.tech.user.domain.User;

public interface CampaignRepository extends JpaRepository<Campaign, String> {
    
    List<Campaign> findAllByUserId(int userId);

    Optional<Campaign> findByIdAndUser(String id, User user);
}
