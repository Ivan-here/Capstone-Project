package com.example.notificationsservice.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class IdentityClient {

    private static final String INTERNAL_SECRET_HEADER = "X-Internal-Secret";

    private final WebClient webClient;
    private final String identityBaseUrl;
    private final String internalSharedSecret;

    public IdentityClient(
            WebClient webClient,
            @Value("${services.identity.base-url}") String identityBaseUrl,
            @Value("${internal.sharedSecret}") String internalSharedSecret
    ) {
        this.webClient = webClient;
        this.identityBaseUrl = identityBaseUrl;
        this.internalSharedSecret = internalSharedSecret;
    }

    public IdentityUserSummary getUserSummary(String userId) {
        try {
            return webClient.get()
                    .uri(identityBaseUrl + "/internal/users/" + userId + "/summary")
                    .header(INTERNAL_SECRET_HEADER, internalSharedSecret)
                    .retrieve()
                    .bodyToMono(IdentityUserSummary.class)
                    .block();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return null;
            }
            throw e;
        }
    }

    public record IdentityUserSummary(
            String userId,
            String email,
            String username,
            String displayName,
            String firstName,
            String lastName,
            String status,
            java.util.List<String> roles
    ) {}
}
