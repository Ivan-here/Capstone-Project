package com.example.adminservice.dtos.community;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CommunityPost {
    private String id;
    private String userId;
    private String authorDisplayName;
    private String authorUsername;
    private String authorType;
    private String authorHeadline;
    private String tab;
    private String visibility;
    private String title;
    private String content;
    private String imageUrl;
    private List<String> tags;
    private List<CommunityComment> comments;
    private List<CommunityReaction> reactions;
    private long likeCount;
    private long dislikeCount;
    private long lastLikeMilestoneNotified;
    private Instant createdAt;
    private Instant updatedAt;
}
