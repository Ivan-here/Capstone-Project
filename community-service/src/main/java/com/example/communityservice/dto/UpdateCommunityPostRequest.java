package com.example.communityservice.dto;

import com.example.communityservice.model.CommunityAuthorType;
import com.example.communityservice.model.CommunityTab;
import com.example.communityservice.model.CommunityVisibility;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UpdateCommunityPostRequest(
        @NotBlank(message = "userId is required") String userId,
        @NotBlank(message = "title is required") String title,
        @NotBlank(message = "content is required") String content,
        CommunityTab tab,
        CommunityVisibility visibility,
        CommunityAuthorType authorType,
        String authorHeadline,
        String imageUrl,
        String ctaText,
        String ctaUrl,
        List<String> tags
) {}
