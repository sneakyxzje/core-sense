package com.insight_pulse.tech.email.dto;


import java.util.Map;

public record SendEmailRequest(
    String submissionId,    
    String templateSlug,  
    String subject,       
    String customBody,    
    Map<String, Object> variables
) {}