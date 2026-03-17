package com.example.identityservice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class InternalServiceAuthFilter extends OncePerRequestFilter {

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";
    private final String sharedSecret;

    public InternalServiceAuthFilter(@Value("${internal.sharedSecret}") String sharedSecret) {
        this.sharedSecret = sharedSecret;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri == null || !uri.startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            String headerSecret = request.getHeader(INTERNAL_SECRET_HEADER);
            if (headerSecret != null && !headerSecret.isBlank() && headerSecret.equals(sharedSecret)) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        "internal-service",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_INTERNAL"))
                );
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }

        filterChain.doFilter(request, response);
    }
}
