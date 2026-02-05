package com.insight_pulse.tech.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.insight_pulse.tech.payment.domain.enums.PaymentStatus;
import com.insight_pulse.tech.subscription.domain.Subscription;
import com.insight_pulse.tech.user.domain.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "payments")
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private BigDecimal amount;

    private String orderId;

    @ManyToOne
    @JoinColumn(name="customer")
    private User user;

    @ManyToOne
    @JoinColumn(name="subscription_id")
    private Subscription subscription;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status;

    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime updatedAt;

    public static Payment create(BigDecimal amount, String orderId, User user, Subscription subscription, PaymentStatus status) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setOrderId(orderId);
        payment.setUser(user);
        payment.setSubscription(subscription);
        payment.setStatus(status);
        return payment;
    }
}
