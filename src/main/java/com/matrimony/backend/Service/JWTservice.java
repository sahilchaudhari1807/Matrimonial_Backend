
package com.matrimony.backend.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class JWTservice {

    private final String secretKey;

    public JWTservice(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = secretKey;

        if (secretKey.getBytes(StandardCharsets.UTF_8).length < 32) {
            log.error("JWT secret configuration is invalid: key is too short");
            throw new IllegalArgumentException(
                    "JWT secret must be at least 32 bytes long"
            );
        }

        log.info("JWT service initialized successfully");
    }

    // Generate JWT
    public String generateToken(String username) {

        log.debug("Generating JWT for user: {}", username);

        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        log.debug("JWT generated successfully for user: {}", username);

        return token;
    }

    // Generate signing key
    private SecretKey getSignKey() {

        log.debug("Creating JWT signing key");

        byte[] bytes = secretKey.getBytes(StandardCharsets.UTF_8);

        return Keys.hmacShaKeyFor(bytes);
    }

    // Extract username
    public String extractUserName(String token) {

        Claims claims = extractAllClaims(token);

        log.debug("Username extracted from JWT");

        return claims.getSubject();
    }

    // Validate JWT
    public boolean validateToken(String token, UserDetails userDetail) {

        String username = extractUserName(token);

        boolean valid = username.equals(userDetail.getUsername())
                && !isTokenExpired(token);

        log.debug("JWT validation completed: {}", valid);

        return valid;
    }

    // Extract claims
    private Claims extractAllClaims(String token) {

        log.debug("Verifying JWT signature and extracting claims");

        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check expiration
    private boolean isTokenExpired(String token) {

        boolean expired = extractAllClaims(token)
                .getExpiration()
                .before(new Date());

        log.debug("JWT expiration check completed: expired={}", expired);

        return expired;
    }
}
