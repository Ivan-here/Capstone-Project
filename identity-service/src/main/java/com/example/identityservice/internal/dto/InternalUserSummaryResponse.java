package com.example.identityservice.internal.dto;

import java.util.List;

public record InternalUserSummaryResponse(
        String userId,
        String email,
        String username,
        String displayName,
        String firstName,
        String lastName,
        String status,
        List<String> roles
) {}
