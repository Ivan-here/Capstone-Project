package com.example.communityservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "community_posts")
public class CommunityPost {

    @Id
    private String id;

    private String userId;
    private String authorDisplayName;
    private String authorUsername;
    private CommunityAuthorType authorType = CommunityAuthorType.USER;
    private String authorHeadline;

    private CommunityTab tab = CommunityTab.COMMUNITY;
    private CommunityVisibility visibility = CommunityVisibility.PUBLIC;

    private String title;
    private String content;
    private String imageUrl;
    private String ctaText;
    private String ctaUrl;

    private List<String> tags = new ArrayList<>();
    private List<CommunityComment> comments = new ArrayList<>();
    private long likeCount = 0;

    private Instant createdAt;
    private Instant updatedAt;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAuthorDisplayName() { return authorDisplayName; }
    public void setAuthorDisplayName(String authorDisplayName) { this.authorDisplayName = authorDisplayName; }

    public String getAuthorUsername() { return authorUsername; }
    public void setAuthorUsername(String authorUsername) { this.authorUsername = authorUsername; }

    public CommunityAuthorType getAuthorType() { return authorType; }
    public void setAuthorType(CommunityAuthorType authorType) { this.authorType = authorType; }

    public String getAuthorHeadline() { return authorHeadline; }
    public void setAuthorHeadline(String authorHeadline) { this.authorHeadline = authorHeadline; }

    public CommunityTab getTab() { return tab; }
    public void setTab(CommunityTab tab) { this.tab = tab; }

    public CommunityVisibility getVisibility() { return visibility; }
    public void setVisibility(CommunityVisibility visibility) { this.visibility = visibility; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCtaText() { return ctaText; }
    public void setCtaText(String ctaText) { this.ctaText = ctaText; }

    public String getCtaUrl() { return ctaUrl; }
    public void setCtaUrl(String ctaUrl) { this.ctaUrl = ctaUrl; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<CommunityComment> getComments() { return comments; }
    public void setComments(List<CommunityComment> comments) { this.comments = comments; }

    public long getLikeCount() { return likeCount; }
    public void setLikeCount(long likeCount) { this.likeCount = likeCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
