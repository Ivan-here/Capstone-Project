package com.example.adminservice.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.*;

class PublicBoundarySecurityTest {
    private final PublicBoundaryFilter filter = new PublicBoundaryFilter();
    @Test void blocksEveryInternalPathAndEncodedVariants() {
        for(String path : java.util.List.of("/internal/profiles/u/verify", "/api/community/posts/internal/user/u",
                "/profiles/me/settings/internal/account/u", "/api/payments/release", "/api/payments/refund/",
                "/api/payments/verify", "/actuator/env", "/api/listings/%69nternal/owner/u", "/api/listings;foo/admin")) {
            var exchange = MockServerWebExchange.from(MockServerHttpRequest.get(path));
            AtomicBoolean reached = new AtomicBoolean();
            filter.filter(exchange, e -> { reached.set(true); return Mono.empty(); }).block();
            assertFalse(reached.get(), path);
            assertTrue(exchange.getResponse().getStatusCode().is4xxClientError());
        }
    }
    @Test void stripsForgedServiceHeadersButPreservesBearerAndWebhookSignature() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/payments/webhook")
            .header("X-Internal-Secret", "forged").header("X-User-Id", "victim")
            .header("Authorization", "Bearer token").header("Stripe-Signature", "signature"));
        filter.filter(exchange, e -> {
            assertNull(e.getRequest().getHeaders().getFirst("X-Internal-Secret"));
            assertNull(e.getRequest().getHeaders().getFirst("X-User-Id"));
            assertEquals("Bearer token", e.getRequest().getHeaders().getFirst("Authorization"));
            assertEquals("signature", e.getRequest().getHeaders().getFirst("Stripe-Signature"));
            return Mono.empty();
        }).block();
    }
    @Test void boundsRequestBodies() {
        var exchange = MockServerWebExchange.from(MockServerHttpRequest.post("/api/orders").header("Content-Length", "1048577"));
        filter.filter(exchange, e -> { fail("oversized body reached service"); return Mono.empty(); }).block();
        assertEquals(HttpStatus.PAYLOAD_TOO_LARGE, exchange.getResponse().getStatusCode());
    }
}
