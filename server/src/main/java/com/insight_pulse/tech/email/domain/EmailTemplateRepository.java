package com.insight_pulse.tech.email.domain;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long>{
    Optional<EmailTemplate> findBySlug(String slug);
}
