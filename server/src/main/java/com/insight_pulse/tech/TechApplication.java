package com.insight_pulse.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.insight_pulse.tech.security.config.JwtConfigProperties;

@SpringBootApplication
@EnableConfigurationProperties(JwtConfigProperties.class)
@EnableScheduling
@EnableAsync
public class TechApplication {

	public static void main(String[] args) {
		SpringApplication.run(TechApplication.class, args);
	}

}
