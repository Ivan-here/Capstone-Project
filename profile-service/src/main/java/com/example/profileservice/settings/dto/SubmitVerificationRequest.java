package com.example.profileservice.settings.dto;

public record SubmitVerificationRequest(
        String userId,
        String type
) {
}
