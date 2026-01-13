package com.insight_pulse.tech.email.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.insight_pulse.tech.email.domain.EmailTemplate;
import com.insight_pulse.tech.email.domain.EmailTemplateRepository;
import com.insight_pulse.tech.email.dto.EmailTemplateResponse;
import com.insight_pulse.tech.submission.domain.Submission;
import com.insight_pulse.tech.submission.domain.SubmissionRepository;

import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;
    private final EmailTemplateRepository templateRepository;
    private final SubmissionRepository submissionRepository;

    private String replacePlaceholders(String content, Map<String, Object> data) {
        if (content == null) return "";
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                content = content.replace("[[" + entry.getKey() + "]]", entry.getValue().toString());
            }
        return content;
    }

    public void send(String from, String to, String subject, String content) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom(new InternetAddress(from));
                mimeMessage.setSubject(subject, "UTF-8"); 
                mimeMessage.setText(content, "UTF-8", "html");
            }
        };
        try {
            javaMailSender.send(preparator);
        } catch(MailException ex) {
            log.error("Sending mail error to {}", to, ex);
        }
    }

    private String renderTemplate(String templateName, Map<String, Object> data) {
        Context context = new Context();
        context.setVariables(data);
        return templateEngine.process(templateName, context);
    }

    public void sendWithTemplate(String from, String to, String subject, Long templateId, Map<String, Object> data) {
        EmailTemplate emailTemplate = templateRepository.findById(templateId)
        .orElseThrow(() -> new RuntimeException("Template not found"));
        String htmlContent = renderTemplate(emailTemplate.getTemplateFile(), data);
        send(from, to, subject, htmlContent);
    }

    @Async
    public void sendFlexible(String from, String subject, String customBody, String templateSlug, Map<String, Object> variables, String submissionId) {
        if (variables == null) variables = new HashMap<>();
        Submission submission = submissionRepository.findById(submissionId)
        .orElseThrow(() -> new RuntimeException("Submission not found"));
        variables.put("candidate_name", submission.getFullname());
        System.out.println("DEBUG: Submission ID = " + submission.getId());
        System.out.println("DEBUG: Fullname from DB = [" + submission.getFullname() + "]");
        String processed = replacePlaceholders(customBody, variables);
        String finalTemplate;
        if(templateSlug != null && !templateSlug.isEmpty()) {
            EmailTemplate emailTemplate = templateRepository.findBySlug(templateSlug)
            .orElseThrow(() -> new RuntimeException("Template not found"));

            finalTemplate = emailTemplate.getTemplateFile();
        } else {
            finalTemplate = "default/default-layout";
        }
        variables.put("bodyContent", processed);

        String htmlContent = renderTemplate(finalTemplate, variables);
        send(from, submission.getEmail(), subject,htmlContent);
    }

    public List<EmailTemplateResponse> getAllTemplate() {
        List<EmailTemplate> emailTemplate = templateRepository.findAll();
        return emailTemplate.stream().map(e -> new EmailTemplateResponse(
            e.getSlug(),
            e.getName(),
            e.getSubject(),
            e.getDescription(),
            e.getCustomBody()
        )).toList();
    }
}
