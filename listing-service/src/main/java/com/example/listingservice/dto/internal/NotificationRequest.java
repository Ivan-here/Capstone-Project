package com.example.listingservice.dto.internal;

public record NotificationRequest(
        String userId,
        String actorUserId,
        String type,
        String title,
        String message,
        String sourceService,
        String referenceType,
        String referenceId,
        String targetUrl
) {}
