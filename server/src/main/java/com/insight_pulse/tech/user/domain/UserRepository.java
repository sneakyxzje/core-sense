package com.insight_pulse.tech.user.domain;


import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    
    User findByEmail(String email);
}
