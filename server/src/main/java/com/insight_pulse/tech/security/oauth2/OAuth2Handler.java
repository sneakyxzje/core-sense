package com.insight_pulse.tech.security.oauth2;

import java.io.IOException;

import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.security.token.JwtTokenProvider;
import com.insight_pulse.tech.user.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OAuth2Handler extends SimpleUrlAuthenticationSuccessHandler{
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");
        String name = oAuth2User.getAttribute("name");
        var user = userService.processOAuthPostLogin(email, name, googleId);
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken();
        jwtTokenProvider.storeToken(String.valueOf(user.getId()), refreshToken);
        ResponseCookie accessTokenCookie = ResponseCookie.from("jwt", token)
            .httpOnly(true)
            .secure(false) 
            .path("/")
            .maxAge(jwtTokenProvider.getExpiryInSeconds())
            .sameSite("Lax")
            .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) 
                .sameSite("Lax" )
                .build();
        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        response.addHeader(org.springframework.http.HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        getRedirectStrategy().sendRedirect(request, response, "http://localhost:5173/campaigns");
    }
}
