package com.insight_pulse.tech.security.token;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.crypto.SecretKey;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.insight_pulse.tech.security.config.JwtConfigProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    
    private final JwtConfigProperties jwtConfigProperties;
    private final StringRedisTemplate redisTemplate;
    private SecretKey secretKey;

    private final long JWT_EXPIRATION = 600000L;

    @PostConstruct
    public void init() {
        String secret = jwtConfigProperties.secret();
        secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
    }

    public String generateToken(int userId, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
        return Jwts.builder().setSubject(String.valueOf(userId)).claim("role", role).setIssuedAt(now).setExpiration(expiryDate).signWith(secretKey).compact();
    }
    
    public String generateRefreshToken() {
        String refreshTokenId = UUID.randomUUID().toString();
        return refreshTokenId;
    }

    public void storeToken(String userId, String refreshToken) {
        redisTemplate.opsForValue().set(refreshToken, userId, 7, TimeUnit.DAYS); 
    }
    
    public void deleteRefreshToken(String refreshToken) {
        redisTemplate.expire(refreshToken, 30, TimeUnit.SECONDS);
    }
    
    public String getUserIdFromRefreshToken(String refreshToken) {
        return redisTemplate.opsForValue().get(refreshToken);
    } 

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject();
    }

    public int getUserIdFromToken(String token) {
        return Integer.parseInt(parseClaims(token).getSubject());
    } 

    public String getRoleFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
        .setSigningKey(secretKey)
        .build()
        .parseClaimsJws(token)
        .getBody();

        return claims.get("role", String.class);
    }

    public long getExpiryInSeconds() {
        return JWT_EXPIRATION / 1000;
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        }
        catch(ExpiredJwtException ex) {
            System.out.println("Token expired");
        } catch(UnsupportedJwtException ex) {
            System.out.println("Unsupported Token");
        } catch (MalformedJwtException ex) {
            System.out.println("JWT wrong structure");
        } catch(IllegalArgumentException ex) {
            System.out.println("Illegal JWT");
        }
        return false;
    }
}
