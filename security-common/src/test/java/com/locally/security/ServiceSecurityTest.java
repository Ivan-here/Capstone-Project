package com.locally.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = ServiceSecurityTest.App.class, properties = {
    "JWT_SECRET=test-jwt-secret-only-01234567890123456789",
    "INTERNAL_SHARED_SECRET=test-internal-secret-only-01234567890123"
})
@AutoConfigureMockMvc
class ServiceSecurityTest {
    static final String JWT = "test-jwt-secret-only-01234567890123456789";
    static final String INTERNAL = "test-internal-secret-only-01234567890123";
    @SpringBootConfiguration @EnableAutoConfiguration
    @Import({ServiceSecurityConfiguration.class, Routes.class})
    static class App {
        @Bean @org.springframework.context.annotation.Primary AccountLookup accounts() { return mock(AccountLookup.class); }
    }
    @RestController static class Routes {
        @GetMapping("/api/listings") String publicRead() { return "ok"; }
        @PostMapping("/api/orders") String write() { return Access.userId(); }
        @GetMapping("/admin/users") String admin() { return "admin"; }
        @PostMapping("/internal/profiles/u/verify") String internal() { return "internal"; }
        @DeleteMapping("/profiles/me/settings/internal/account/u") String nestedInternal() { return "internal"; }
        @PostMapping("/api/payments/release") String payment() { return "payment"; }
        @GetMapping("/notifications") String own(@RequestParam String userId) { Access.owner(userId); return "own"; }
        @PostMapping("/api/payments/webhook") String webhook() { return "webhook"; }
    }
    @Autowired MockMvc mvc;
    @Autowired AccountLookup accounts;
    @BeforeEach void resetAccounts() {
        reset(accounts);
        when(accounts.find("alice")).thenReturn(new AccountLookup.Account("alice", "ACTIVE", List.of("SHOPPER")));
    }
    String token(String key, Instant expiry, List<String> roles) {
        var builder = Jwts.builder().setSubject("alice").claim("roles", roles);
        if (expiry != null) builder.setExpiration(Date.from(expiry));
        return builder.signWith(Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8)), SignatureAlgorithm.HS256).compact();
    }
    String bearer() { return "Bearer " + token(JWT, Instant.now().plusSeconds(60), List.of("SHOPPER")); }
    @Test void publicReadsRemainAvailable() throws Exception { mvc.perform(get("/api/listings")).andExpect(status().isOk()); }
    @Test void anonymousWriteIsDenied() throws Exception { mvc.perform(post("/api/orders")).andExpect(status().isUnauthorized()); }
    @Test void forgedSignatureIsDenied() throws Exception {
        mvc.perform(post("/api/orders").header("Authorization", "Bearer " + token(INTERNAL, Instant.now().plusSeconds(60), List.of("ADMIN"))))
            .andExpect(status().isUnauthorized());
        verifyNoInteractions(accounts);
    }
    @Test void expiredAndUnboundedTokensAreDenied() throws Exception {
        for (Instant expiry : new Instant[]{Instant.now().minusSeconds(5), null})
            mvc.perform(post("/api/orders").header("Authorization", "Bearer " + token(JWT, expiry, List.of("SHOPPER"))))
                .andExpect(status().isUnauthorized());
    }
    @Test void validBearerAuthenticates() throws Exception { mvc.perform(post("/api/orders").header("Authorization", bearer())).andExpect(status().isOk()).andExpect(content().string("alice")); }
    @Test void currentRolesOverrideOldAdminClaim() throws Exception {
        mvc.perform(get("/admin/users").header("Authorization", "Bearer " + token(JWT, Instant.now().plusSeconds(60), List.of("ADMIN"))))
            .andExpect(status().isForbidden());
    }
    @Test void suspendedUserIsImmediatelyRejected() throws Exception {
        when(accounts.find("alice")).thenReturn(new AccountLookup.Account("alice", "SUSPENDED", List.of("ADMIN")));
        mvc.perform(post("/api/orders").header("Authorization", bearer())).andExpect(status().isUnauthorized());
    }
    @Test void identityFailureFailsClosed() throws Exception {
        when(accounts.find("alice")).thenThrow(new IllegalStateException("unavailable"));
        mvc.perform(post("/api/orders").header("Authorization", bearer())).andExpect(status().isServiceUnavailable());
    }
    @Test void anotherUsersNotificationIsForbidden() throws Exception {
        mvc.perform(get("/notifications?userId=bob").header("Authorization", bearer())).andExpect(status().isForbidden());
        mvc.perform(get("/notifications?userId=alice").header("Authorization", bearer())).andExpect(status().isOk());
    }
    @Test void browserCannotCallInternalOperationsEvenWithAdminRole() throws Exception {
        when(accounts.find("alice")).thenReturn(new AccountLookup.Account("alice", "ACTIVE", List.of("ADMIN")));
        mvc.perform(post("/internal/profiles/u/verify").header("Authorization", bearer())).andExpect(status().isForbidden());
        mvc.perform(delete("/profiles/me/settings/internal/account/u").header("Authorization", bearer())).andExpect(status().isForbidden());
        mvc.perform(post("/api/payments/release").header("Authorization", bearer())).andExpect(status().isForbidden());
    }
    @Test void correctServiceCredentialAllowsInternalCall() throws Exception {
        mvc.perform(post("/internal/profiles/u/verify").header("X-Internal-Secret", INTERNAL)).andExpect(status().isOk());
    }
    @Test void wrongServiceCredentialFailsClosed() throws Exception {
        mvc.perform(post("/internal/profiles/u/verify").header("X-Internal-Secret", "wrong")).andExpect(status().isUnauthorized());
    }
    @Test void webhookReachesSignatureCheckingControllerWithoutBearer() throws Exception { mvc.perform(post("/api/payments/webhook")).andExpect(status().isOk()); }
    @Test void secretsCannotUseExamplesOrSameValue() {
        assertThrows(IllegalArgumentException.class, () -> new ServiceAuthenticationFilter(JWT, JWT, accounts));
        assertThrows(IllegalArgumentException.class, () -> new ServiceAuthenticationFilter("replace-with-a-long-random-secret", INTERNAL, accounts));
    }
    @Test void serviceCredentialsAreLimitedToPrivateDestinations() {
        assertTrue(InternalClientsConfiguration.trusted(java.net.URI.create("http://listing-service:8084/api/listings")));
        for(String uri : List.of("https://evil.example:8084", "http://listing-service.evil.example:8084", "http://localhost:9000", "http://user@listing-service:8084"))
            assertFalse(InternalClientsConfiguration.trusted(java.net.URI.create(uri)));
    }
}
