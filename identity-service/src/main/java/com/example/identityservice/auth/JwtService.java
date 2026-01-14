package com.example.identityservice.auth;

import com.example.identityservice.users.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;

@Slf4j
@Service
public class JwtService {

    private final SecretKey key;
    private final long accessSeconds;

    public JwtService(
            @Value("${security.jwt.secret}") String secret,
            @Value("${security.jwt.accessTokenSeconds:3600}") long accessSeconds
    ) {
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("security.jwt.secret must be at least 32 characters");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessSeconds = accessSeconds;
    }

    public String issueAccessToken(String userId, Collection<Role> roles) {
        Instant now = Instant.now();

        return Jwts.builder()
                .setSubject(userId)                 // sub = userId
                .claim("roles", roles)              // roles claim
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(accessSeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public long accessTokenTtlSeconds() {
        return accessSeconds;
    }
}