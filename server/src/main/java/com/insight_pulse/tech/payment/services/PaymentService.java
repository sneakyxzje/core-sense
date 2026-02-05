package com.insight_pulse.tech.payment.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.stereotype.Service;

import com.insight_pulse.tech.config.VnPayConfig;
import com.insight_pulse.tech.payment.domain.Payment;
import com.insight_pulse.tech.payment.domain.PaymentRepository;
import com.insight_pulse.tech.payment.domain.enums.PaymentStatus;
import com.insight_pulse.tech.payment.dto.PaymentRequest;
import com.insight_pulse.tech.payment.utils.VNPayUtils;
import com.insight_pulse.tech.quotas.domain.Quota;
import com.insight_pulse.tech.quotas.domain.QuotaRepository;
import com.insight_pulse.tech.security.context.CurrentUserProvider;
import com.insight_pulse.tech.subscription.domain.Subscription;
import com.insight_pulse.tech.subscription.domain.SubscriptionRepository;
import com.insight_pulse.tech.user.domain.User;
import com.insight_pulse.tech.user.domain.UserRepository;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final VnPayConfig vnPayConfig;
    private final CurrentUserProvider currentUserProvider;
    private final QuotaRepository quotaRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    public String createPaymentUrl(HttpServletRequest request, PaymentRequest paymentRequest) {
        int userId = currentUserProvider.getCurrentUserId();
        User user = userRepository.getReferenceById(userId);
        Subscription subscription = subscriptionRepository.findById(paymentRequest.subscriptionId())
        .orElseThrow(() -> new RuntimeException("Plans not found"));
        BigDecimal price = subscription.getPrice();
        String txnRef = "ORDER_" + System.currentTimeMillis();
        Payment payment = Payment.create(price, txnRef, user, subscription, PaymentStatus.PENDING);
        paymentRepository.save(payment);
        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.version());
        vnp_Params.put("vnp_Command", vnPayConfig.command());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.tmnCode());

        long amount = price.multiply(new BigDecimal(100)).longValue();
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", txnRef);
        vnp_Params.put("vnp_OrderInfo", txnRef);
        vnp_Params.put("vnp_OrderType", "other"); 
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.returnUrl());
        vnp_Params.put("vnp_IpAddr", "127.0.0.1");
        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));    
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }
            String queryUrl = query.toString();
            String vnp_SecureHash = VNPayUtils.hmacSHA512(vnPayConfig.hashSecret(), hashData.toString());
            queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
            String paymentUrl = vnPayConfig.payUrl() + "?" + queryUrl;
            return paymentUrl;
    }

    private boolean verifyCallback(Map<String, String> fields) {
        String vnp_secureHash = fields.get("vnp_SecureHash");

        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");

        List<String> fieldNames = new ArrayList<>(fields.keySet());

        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while(itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = fields.get(fieldName);

            if((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                if(itr.hasNext()) {
                    hashData.append("&");
                }
            }
        }
        String builtHash = VNPayUtils.hmacSHA512(vnPayConfig.hashSecret(), hashData.toString());
        return builtHash.equalsIgnoreCase(vnp_secureHash);
    }

    public void processCallback(Map<String, String> fields) {
        if(!verifyCallback(fields)) {
            log.error("Payment Security Violation: Invalid Signature");
            return;
        }

        String txnRef = fields.get("vnp_TxnRef");
        String respCode = fields.get("vnp_ResponseCode");
        if("00".equals(respCode)) {
            fulfillPayment(txnRef);
        } else {
            log.error("Payment failed!");
        }
    }

    @Transactional
    private void fulfillPayment(String txnRef) {
        Payment payment = paymentRepository.findByOrderId(txnRef);
        if(payment.getStatus() == PaymentStatus.SUCCESS) {
            log.error("Payment not found for reference: {}", txnRef);
            return;
        }
        Quota quota = quotaRepository.findByUserId(payment.getUser().getId())
        .orElseThrow(() -> new RuntimeException("Quota record missing for user"));
        quota.setSubscription(payment.getSubscription());
        quota.setUpdatedAt(LocalDateTime.now());
        quotaRepository.save(quota);

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setUpdatedAt(LocalDateTime.now());;
        paymentRepository.save(payment);

    }

    public Map<String, Object> getPaymentStatus(String txnRef) {
        Payment payment = paymentRepository.findByOrderId(txnRef);
        Map<String, Object> result = new HashMap<>();
        result.put("status", payment.getStatus());
        return result;
    }
}
