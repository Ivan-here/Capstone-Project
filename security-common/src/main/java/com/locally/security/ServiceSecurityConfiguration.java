package com.locally.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestClient;

@Configuration(proxyBeanMethods = false)
@Import(InternalClientsConfiguration.class)
public class ServiceSecurityConfiguration {
    @Bean
    @ConditionalOnMissingBean(AccountLookup.class)
    AccountLookup accountLookup(@Value("${security.identity-url:http://localhost:8082}") String identityUrl,
                                @Value("${INTERNAL_SHARED_SECRET}") String secret) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(5000);
        var client = RestClient.builder().baseUrl(identityUrl).requestFactory(factory)
                .defaultHeader("X-Internal-Secret", secret).build();
        return userId -> {
            try { return client.get().uri("/internal/users/{id}/summary", userId).retrieve().body(AccountLookup.Account.class); }
            catch (org.springframework.web.client.HttpClientErrorException.NotFound missing) { return null; }
        };
    }

    @Bean
    SecurityFilterChain serviceSecurity(HttpSecurity http, AccountLookup accounts,
            @Value("${JWT_SECRET:${security.jwt.secret:}}") String jwt,
            @Value("${INTERNAL_SHARED_SECRET}") String internal) throws Exception {
        http.csrf(csrf -> csrf.disable()) // API accepts bearer tokens only; cookies never authenticate requests.
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .requestCache(c -> c.disable())
            .formLogin(c -> c.disable()).httpBasic(c -> c.disable()).logout(c -> c.disable())
            .exceptionHandling(e -> e.authenticationEntryPoint((req, res, ex) -> res.sendError(401)))
            .authorizeHttpRequests(a -> a
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register", "/api/payments/webhook").permitAll()
                .requestMatchers(HttpMethod.GET, "/actuator/health", "/api/payments/config", "/api/stripe/onboarding/return", "/api/stripe/onboarding/refresh").permitAll()
                .requestMatchers(req -> java.util.Arrays.asList(req.getRequestURI().split("/")).contains("internal")).hasRole("INTERNAL")
                .requestMatchers("/api/payments/intent", "/api/payments/release", "/api/payments/refund", "/api/payments/verify", "/api/payments/cancel").hasRole("INTERNAL")
                .requestMatchers("/users/**", "/admin/**", "/api/verification/admin/**", "/api/listings/admin").hasAnyRole("ADMIN", "INTERNAL")
                .requestMatchers(HttpMethod.POST, "/notifications").hasAnyRole("ADMIN", "INTERNAL")
                .requestMatchers(HttpMethod.PUT, "/notifications/**").hasAnyRole("ADMIN", "INTERNAL")
                .requestMatchers("/profiles/me", "/profiles/me/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/profiles/{id}", "/api/listings", "/api/listings/{id}", "/api/reviews/**").permitAll()
                .requestMatchers("/actuator/**", "/error").denyAll()
                .anyRequest().authenticated())
            .addFilterBefore(new ServiceAuthenticationFilter(jwt, internal, accounts), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
