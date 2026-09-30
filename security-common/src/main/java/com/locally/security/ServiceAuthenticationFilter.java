package com.locally.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

public final class ServiceAuthenticationFilter extends OncePerRequestFilter {
    private final SecretKey key;
    private final byte[] internalSecret;
    private final AccountLookup accounts;

    public ServiceAuthenticationFilter(String jwtSecret, String sharedSecret, AccountLookup accounts) {
        validateSecret(jwtSecret);
        validateSecret(sharedSecret);
        if (jwtSecret.equals(sharedSecret)) throw new IllegalArgumentException("JWT and internal secrets must differ");
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        this.internalSecret = sharedSecret.getBytes(StandardCharsets.UTF_8);
        this.accounts = accounts;
    }

    private static void validateSecret(String secret) {
        if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32
                || secret.contains("replace-with") || secret.contains("change-me")) {
            throw new IllegalArgumentException("Configure independent random secrets of at least 32 bytes");
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String supplied = request.getHeader("X-Internal-Secret");
        String bearer = request.getHeader("Authorization");
        try {
            if (supplied != null) {
                if (!MessageDigest.isEqual(internalSecret, supplied.getBytes(StandardCharsets.UTF_8))) {
                    response.sendError(401); return;
                }
                authenticate("internal-service", List.of("INTERNAL"));
            } else if (bearer != null) {
                if (!bearer.startsWith("Bearer ") || bearer.length() > 8192) {
                    response.sendError(401); return;
                }
                Claims claims;
                try {
                    var signed = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(bearer.substring(7));
                    claims = signed.getBody();
                    if (!"HS256".equals(signed.getHeader().getAlgorithm()) || claims.getExpiration() == null
                            || claims.getSubject() == null || !claims.getSubject().matches("[A-Za-z0-9_-]{1,128}")) {
                        response.sendError(401); return;
                    }
                } catch (RuntimeException invalidToken) {
                    response.sendError(401); return;
                }
                // Current database roles/status revoke suspended accounts and removed roles immediately.
                AccountLookup.Account account;
                try { account = accounts.find(claims.getSubject()); }
                catch (RuntimeException unavailable) { response.sendError(503); return; }
                if (account == null || !claims.getSubject().equals(account.userId()) || !"ACTIVE".equals(account.status())) {
                    response.sendError(401); return;
                }
                authenticate(account.userId(), account.roles() == null ? List.of() : account.roles().stream().filter(r -> !"INTERNAL".equals(r)).toList());
                if (!List.of("GET", "HEAD", "OPTIONS").contains(request.getMethod())) {
                    try { AttemptLimiter.check("writes:" + account.userId(), 120, 60); }
                    catch (org.springframework.web.server.ResponseStatusException limit) { response.sendError(429); return; }
                }
            }
            chain.doFilter(request, response);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    private void authenticate(String subject, List<String> roles) {
        var authorities = roles.stream().filter(r -> List.of("SHOPPER", "FARMER", "RESTAURANT", "NGO", "ADMIN", "INTERNAL").contains(r))
                .map(r -> new SimpleGrantedAuthority("ROLE_" + r)).toList();
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(subject, null, authorities));
    }
}
