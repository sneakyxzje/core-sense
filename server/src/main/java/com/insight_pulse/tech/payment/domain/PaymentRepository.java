package com.insight_pulse.tech.payment.domain;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {
   
    Payment findByOrderId(String orderId);
}
