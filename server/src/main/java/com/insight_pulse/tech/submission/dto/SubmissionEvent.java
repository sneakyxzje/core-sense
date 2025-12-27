package com.insight_pulse.tech.submission.dto;

import java.time.LocalDateTime;

public record SubmissionEvent(
    String campaignName,
    String submissionId,   
    String previewText,    
    long newTotal,         
    LocalDateTime time      
) {}