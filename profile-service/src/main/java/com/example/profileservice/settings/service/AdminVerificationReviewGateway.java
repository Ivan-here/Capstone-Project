package com.example.profileservice.settings.service;

import com.example.profileservice.settings.dto.AdminVerificationReviewRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminVerificationReviewGateway {

    private final WebClient webClient;

    @Value("${clients.adminBaseUrl}")
    private String adminBaseUrl;

    public void notifyReviewRequested(AdminVerificationReviewRequest request) {
        try {
            webClient.post()
                    .uri(adminBaseUrl + "/internal/admin/verifications/review-request")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception ex) {
            log.warn("Failed to notify admin-service about verification review request for userId={}", request.userId(), ex);
        }
    }
}
