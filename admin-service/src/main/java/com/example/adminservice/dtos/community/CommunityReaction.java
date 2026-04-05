package com.example.adminservice.dtos.community;

import lombok.Data;

import java.time.Instant;

@Data
public class CommunityReaction {
    private String userId;
    private String displayName;
    private String username;
    private String reactionType;
    private Instant reactedAt;
}
