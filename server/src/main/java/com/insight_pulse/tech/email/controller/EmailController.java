package com.insight_pulse.tech.email.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.insight_pulse.tech.email.dto.EmailTemplateResponse;
import com.insight_pulse.tech.email.dto.SendEmailRequest;
import com.insight_pulse.tech.email.service.EmailService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/emails")
public class EmailController {
    
    private final EmailService emailService;

    @PostMapping
    public ResponseEntity<String> sendEmailToCandidate(@RequestBody SendEmailRequest request) {
        String senderEmail = "no-reply@coresense.com";
        emailService.sendFlexible(
            senderEmail,
            request.subject(),
            request.customBody(),
            request.templateSlug(),
            request.variables(),
            request.submissionId()
        );
        return ResponseEntity.ok("Yêu cầu gửi mail đã được tiếp nhận và đang xử lý!");
    }

    @GetMapping
    public ResponseEntity<List<EmailTemplateResponse>> getAllTemplate() {
        return ResponseEntity.ok(emailService.getAllTemplate());
    }
}
