package com.insight_pulse.tech.security.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.insight_pulse.tech.security.filter.JwtAuthFilter;
import com.insight_pulse.tech.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.insight_pulse.tech.security.oauth2.OAuth2Handler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter; 
    private final OAuth2Handler oAuth2Handler;
    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:3000", "https://localhost:3000"));
        config.setAllowCredentials(true);
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-API-KEY"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .oauth2Login(oauth2 -> oauth2.successHandler(oAuth2Handler))
                .oauth2Login(oauth2 -> oauth2.authorizationEndpoint(authorization -> authorization.authorizationRequestRepository(httpCookieOAuth2AuthorizationRequestRepository)))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                        .exceptionHandling(exception -> exception
                            .authenticationEntryPoint((request, response, authException) -> {
                                if(request.getRequestURI().startsWith("/api/")) {
                                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                    response.getWriter().write("Token expired or Invalid");
                                } else {
                                    response.sendRedirect("/oauth2/authorization/google");
                                }
                            })
                        )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/register", "/api/auth/login", "/api/auth/refresh").permitAll()
                        .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll() 
                        .requestMatchers(HttpMethod.POST, "/api/submissions/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/submissions/**").permitAll()
                        .requestMatchers("/api/payment/vnpay-callback").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/feedback/submit").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/gemini/**").authenticated()
                        .requestMatchers("/api/dashboard/**").authenticated()
                        .requestMatchers("/api/channel/**").authenticated()
                        .requestMatchers("/api/user/info").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

}