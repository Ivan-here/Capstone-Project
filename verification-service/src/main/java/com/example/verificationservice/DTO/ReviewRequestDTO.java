package com.example.verificationservice.DTO;

import lombok.Builder;

@Builder
public record ReviewRequestDTO(
        String status,     // e.g., "APPROVED" or "REJECTED"
        String adminNotes  // Reason for approval or rejection
) {}