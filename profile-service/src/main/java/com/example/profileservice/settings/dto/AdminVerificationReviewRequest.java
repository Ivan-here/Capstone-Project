package com.example.profileservice.settings.dto;

public record AdminVerificationReviewRequest(
        String verificationId,
        String userId,
        String type,
        String documentUrl,
        String requestedBy
) {
}
