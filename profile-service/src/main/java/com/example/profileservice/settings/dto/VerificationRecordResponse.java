package com.example.profileservice.settings.dto;

import java.time.LocalDateTime;

public record VerificationRecordResponse(
        String id,
        String userId,
        String type,
        String status,
        String documentUrl,
        String adminNotes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
