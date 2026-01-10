package com.insight_pulse.tech.subscription.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "subscriptions")
public class Subscription {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT") 
    private String description;

    @Column(nullable = false)
    private int aiLimit;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private boolean active = true;
}
