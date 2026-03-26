package com.example.communityservice.dto;

import com.example.communityservice.model.CommunityReactionType;
import jakarta.validation.constraints.NotBlank;

public record UpdateCommunityReactionRequest(
        @NotBlank(message = "userId is required") String userId,
        CommunityReactionType reactionType
) {}
