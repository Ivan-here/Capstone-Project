package com.example.communityservice.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateCommunityCommentRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "text is required") String text
) {}
