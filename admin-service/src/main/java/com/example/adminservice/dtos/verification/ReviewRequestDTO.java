package com.example.adminservice.dtos.verification;

import lombok.Builder;

@Builder
public record ReviewRequestDTO(
        String status,     // e.g., "APPROVED" or "REJECTED"
        String adminNotes  // Reason for approval or rejection
) {}