package com.example.profileservice.settings.dto;

public record SettingsOverviewResponse(
        String userId,
        boolean hasPersonalProfile,
        boolean hasBusinessProfile,
        boolean businessVerified,
        boolean hasPendingVerificationReview
) {
}
