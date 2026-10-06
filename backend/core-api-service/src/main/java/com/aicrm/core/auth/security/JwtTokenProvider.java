package com.aicrm.core.auth.security;

import com.aicrm.core.auth.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;
    private final long expirationSeconds;

    public JwtTokenProvider(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.expiration-seconds:3600}") long expirationSeconds
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationSeconds = expirationSeconds;
    }

    public String generateToken(AuthenticatedUser user) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .subject(String.valueOf(user.userId()))
                .claim("email", user.email())
                .claim("name", user.name())
                .claim("role", user.role().name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .signWith(secretKey)
                .compact();
    }

    public AuthenticatedUser parse(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return new AuthenticatedUser(
                Long.parseLong(claims.getSubject()),
                claims.get("email", String.class),
                claims.get("name", String.class),
                UserRole.valueOf(claims.get("role", String.class))
        );
    }
}
