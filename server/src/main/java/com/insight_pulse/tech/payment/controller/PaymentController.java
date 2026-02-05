package com.insight_pulse.tech.payment.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.payment.dto.PaymentRequest;
import com.insight_pulse.tech.payment.services.PaymentService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
@Slf4j
public class PaymentController {
    

    private final PaymentService paymentService;
    @PostMapping
    public ResponseEntity<?> createPaymentUrl(HttpServletRequest request, @RequestBody PaymentRequest paymentRequest) {
        String response = paymentService.createPaymentUrl(request, paymentRequest);
        Map<String, String> responseData = new HashMap<>();
        responseData.put("data",response);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
    }

    @GetMapping("/vnpay-callback")
    public ResponseEntity<Map<String, String>> vnpayCallback(@RequestParam Map<String, String> params) {
        log.info("This endpoint working");
        paymentService.processCallback(params);
        Map<String, String> response = new HashMap<>();
        response.put("RspCode", "00");
        response.put("Message", "Success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> checkStatus(@RequestParam("vnp_TxnRef") String txnRef) {
        return ResponseEntity.ok(paymentService.getPaymentStatus(txnRef));
    }
}
