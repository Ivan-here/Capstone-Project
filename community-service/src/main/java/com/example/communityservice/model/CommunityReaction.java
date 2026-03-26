package com.example.communityservice.model;

import java.time.Instant;

public class CommunityReaction {

    private String userId;
    private String displayName;
    private String username;
    private CommunityReactionType reactionType;
    private Instant reactedAt;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public CommunityReactionType getReactionType() { return reactionType; }
    public void setReactionType(CommunityReactionType reactionType) { this.reactionType = reactionType; }

    public Instant getReactedAt() { return reactedAt; }
    public void setReactedAt(Instant reactedAt) { this.reactedAt = reactedAt; }
}
