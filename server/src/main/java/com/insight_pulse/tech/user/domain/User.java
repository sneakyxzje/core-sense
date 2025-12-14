package com.insight_pulse.tech.user.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name ="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="fullname", nullable = false, length = 100)
    private String fullname;

    @Column(name="email", nullable = false, length = 100, unique = true)
    private String email;

    @Column(name="password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false, length = 20)
    private Role role;

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;
}
