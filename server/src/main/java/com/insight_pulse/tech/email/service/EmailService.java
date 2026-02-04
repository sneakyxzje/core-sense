package com.insight_pulse.tech.email.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insight_pulse.tech.email.domain.EmailTemplate;
import com.insight_pulse.tech.email.domain.EmailTemplateRepository;
import com.insight_pulse.tech.email.dto.EmailTemplateDTO;
import com.insight_pulse.tech.email.dto.EmailTemplateMetadata;
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
    private final ObjectMapper objectMapper;
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
        String finalTemplate;
        if(templateSlug != null && !templateSlug.isEmpty()) {
            EmailTemplate emailTemplate = templateRepository.findBySlug(templateSlug)
            .orElseThrow(() -> new RuntimeException("Template not found"));

            finalTemplate = emailTemplate.getTemplateFile();
        } else {
            finalTemplate = "default/default-layout";
        }
        String bodyToUse = customBody;
        if(bodyToUse == null || bodyToUse.isEmpty()) {
            try {
                EmailTemplateMetadata metadata = findMetadataBySlug(templateSlug);
                bodyToUse = metadata.defaultBody();
            } catch(Exception e) {
                log.warn("Cannot find metadata, empty content");
                bodyToUse = "";
            }
        }
        String processed = replacePlaceholders(bodyToUse, variables);
        variables.put("bodyContent", processed);

        String htmlContent = renderTemplate(finalTemplate, variables);
        send(from, submission.getEmail(), subject,htmlContent);
    }

    public List<EmailTemplateResponse> getAllTemplate() throws IOException {
        List<EmailTemplate> emailTemplate = templateRepository.findAll();
        List<EmailTemplateMetadata> metadataList = loadAllMetadata();
        return emailTemplate.stream().map(e -> {
            String defaultBody = metadataList.stream()
            .filter(m -> m.id().equals(e.getSlug()))
            .map(EmailTemplateMetadata::defaultBody)
            .findFirst().orElse("");

            return new EmailTemplateResponse(
                e.getSlug(),
                e.getName(),
                e.getSubject(),
                e.getDescription(),
                e.getCustomBody(),
                defaultBody
            );
        }).toList();
    }

   public List<EmailTemplateDTO> getMarketTemplates() throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource metaResource = resolver.getResource("classpath:templates/templates-metadata.json");
    List<EmailTemplateMetadata> metaDataList = objectMapper.readValue(metaResource.getInputStream(), new TypeReference<List<EmailTemplateMetadata>>() {  
    });

    List<EmailTemplateDTO> templates = new ArrayList<>();

    for(EmailTemplateMetadata meta : metaDataList) {
       Resource[] htmlRes = resolver.getResources("classpath:templates/**/" + meta.fileName()); 
       if(htmlRes.length > 0) {
        String content = new String(htmlRes[0].getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        templates.add(new EmailTemplateDTO(
            meta.id(),
            meta.displayName(),
            meta.description(),
            meta.category(),
            content
        ));
       }
    }
    return templates;
   } 

   private EmailTemplateMetadata findMetadataBySlug(String slug) throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource metaResource = resolver.getResource("classpath:templates/templates-metadata.json");
    List<EmailTemplateMetadata> metaDataList = objectMapper.readValue(metaResource.getInputStream(), new TypeReference<List<EmailTemplateMetadata>>() {  
    });
    return metaDataList.stream().filter(m -> m.id().equals(slug))
    .findFirst().orElseThrow(() -> new RuntimeException("Cannot find metadata for slug: " + slug));
   }

   private List<EmailTemplateMetadata> loadAllMetadata() throws IOException {
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource metaResource = resolver.getResource("classpath:templates/templates-metadata.json");
    List<EmailTemplateMetadata> metaDataList = objectMapper.readValue(metaResource.getInputStream(), new TypeReference<List<EmailTemplateMetadata>>() {  
    });
    return metaDataList; 
   }
}
