package com.insight_pulse.tech.notification.domain;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    Page<Notification> findAllByUserId(Pageable pageable, int userId);

    long countByUserIdAndIsReadFalse(int userId);

    Optional<Notification> findByIdAndUserId(long id, int userId);
}
