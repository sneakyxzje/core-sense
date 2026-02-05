package com.insight_pulse.tech.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "vnp")
public record VnPayConfig (
    String tmnCode,
    String hashSecret,
    String payUrl,
    String version,
    String command,
    String returnUrl,
    String ipnUrl
){};
