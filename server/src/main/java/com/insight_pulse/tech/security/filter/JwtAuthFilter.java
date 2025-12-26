package com.insight_pulse.tech.security.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.insight_pulse.tech.security.cookie.JwtCookieExtractor;
import com.insight_pulse.tech.security.principal.UserDetailsImpl;
import com.insight_pulse.tech.security.service.CustomUserDetailsService;
import com.insight_pulse.tech.security.token.JwtTokenProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;

    private final CustomUserDetailsService customUserDetailsService;

    private final JwtCookieExtractor jwtCookieExtractor;
    @Override
    protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response,  FilterChain filterChain) throws ServletException, IOException {
        String path = request.getServletPath();
        if(path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = jwtCookieExtractor.extractToken(request);
        if(token != null && jwtTokenProvider.validateToken(token)) {
            int userId = jwtTokenProvider.getUserIdFromToken(token);
            UserDetailsImpl userDetails = (UserDetailsImpl) customUserDetailsService.loadUserById(userId);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(auth);
            logger.debug("Path " + path);
            logger.debug("Token from cookie: " + token);
            logger.debug("Authentication: " + SecurityContextHolder.getContext().getAuthentication());
        }
        else {
            logger.debug(("Invallid token or token expired"));
        }
        filterChain.doFilter(request, response);
    }
}
