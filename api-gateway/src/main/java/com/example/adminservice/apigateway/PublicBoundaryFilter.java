package com.example.adminservice.apigateway;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/** Public traffic must never become a trusted service request. */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class PublicBoundaryFilter implements WebFilter {
    private static final Set<String> PRIVATE_PAYMENTS = Set.of(
            "/api/payments/intent", "/api/payments/release", "/api/payments/refund", "/api/payments/verify", "/api/payments/cancel");

    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getRawPath();
        var response = exchange.getResponse();
        response.getHeaders().set("X-Content-Type-Options", "nosniff");
        response.getHeaders().set("X-Frame-Options", "DENY");
        response.getHeaders().set("Referrer-Policy", "no-referrer");
        response.getHeaders().set("Cache-Control", "no-store");
        if (path.contains("%") || path.contains(";") || path.contains("\\") || path.contains("//")
                || Arrays.asList(path.split("/")).contains("..") || Arrays.asList(path.split("/")).contains(".")) {
            response.setStatusCode(HttpStatus.BAD_REQUEST); return response.setComplete();
        }
        if (Arrays.asList(path.split("/")).contains("internal")
                || PRIVATE_PAYMENTS.contains(path.replaceAll("/+$", ""))
                || path.startsWith("/actuator") || path.startsWith("/users")) {
            response.setStatusCode(HttpStatus.NOT_FOUND); return response.setComplete();
        }
        long max = exchange.getRequest().getHeaders().getContentType() != null
                && "multipart".equals(exchange.getRequest().getHeaders().getContentType().getType())
                ? 25L * 1024 * 1024 : 1024 * 1024;
        if (exchange.getRequest().getHeaders().getContentLength() > max) {
            response.setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE); return response.setComplete();
        }
        var sanitized = exchange.getRequest().mutate().headers(h -> {
            h.remove("X-Internal-Secret");
            h.remove("X-User-Id");
            h.remove("X-User-Roles");
        }).build();
        var limited = new ServerHttpRequestDecorator(sanitized) {
            public Flux<DataBuffer> getBody() {
                AtomicLong size = new AtomicLong();
                return super.getBody().handle((buffer, sink) -> {
                    if (size.addAndGet(buffer.readableByteCount()) > max) {
                        DataBufferUtils.release(buffer);
                        sink.error(new org.springframework.web.server.ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE));
                    } else sink.next(buffer);
                });
            }
        };
        return chain.filter(exchange.mutate().request(limited).build());
    }
}
