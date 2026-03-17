package com.example.communityservice.service;

import com.example.communityservice.client.IdentityClient;
import com.example.communityservice.client.NotificationClient;
import com.example.communityservice.dto.CreateCommunityCommentRequest;
import com.example.communityservice.dto.CreateCommunityPostRequest;
import com.example.communityservice.dto.UpdateCommunityPostRequest;
import com.example.communityservice.model.*;
import com.example.communityservice.repository.CommunityPostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CommunityPostService {

    private final CommunityPostRepository repository;
    private final IdentityClient identityClient;
    private final NotificationClient notificationClient;

    public CommunityPostService(
            CommunityPostRepository repository,
            IdentityClient identityClient,
            NotificationClient notificationClient
    ) {
        this.repository = repository;
        this.identityClient = identityClient;
        this.notificationClient = notificationClient;
    }

    public CommunityPost create(CreateCommunityPostRequest request) {
        IdentityClient.IdentityUserSummary author = requireUser(request.userId());

        CommunityPost post = new CommunityPost();
        applyPostValues(post, request, author);
        post.setCreatedAt(Instant.now());
        post.setUpdatedAt(Instant.now());

        return repository.save(post);
    }

    public List<CommunityPost> listAll(
            CommunityTab tab,
            CommunityVisibility visibility,
            CommunityAuthorType authorType,
            String userId
    ) {
        List<CommunityPost> posts = userId != null && !userId.isBlank()
                ? repository.findByUserIdOrderByCreatedAtDesc(userId.trim())
                : repository.findAllByOrderByCreatedAtDesc();

        List<CommunityPost> filtered = new ArrayList<>();
        for (CommunityPost post : posts) {
            if (tab != null && post.getTab() != tab) {
                continue;
            }
            if (visibility != null && post.getVisibility() != visibility) {
                continue;
            }
            if (authorType != null && post.getAuthorType() != authorType) {
                continue;
            }
            filtered.add(post);
        }
        return filtered;
    }

    public List<CommunityPost> listByUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        return repository.findByUserIdOrderByCreatedAtDesc(userId.trim());
    }

    public CommunityPost getById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Community post not found: " + id));
    }

    public CommunityPost update(String id, UpdateCommunityPostRequest request) {
        IdentityClient.IdentityUserSummary author = requireUser(request.userId());

        CommunityPost existing = getById(id);
        applyPostValues(existing, request, author);
        existing.setUpdatedAt(Instant.now());

        return repository.save(existing);
    }

    public void delete(String id) {
        CommunityPost existing = getById(id);
        repository.delete(existing);
    }

    public List<CommunityComment> listComments(String postId) {
        CommunityPost post = getById(postId);
        return post.getComments() != null ? post.getComments() : List.of();
    }

    public CommunityPost addComment(String postId, CreateCommunityCommentRequest request) {
        IdentityClient.IdentityUserSummary commenter = requireUser(request.userId());
        CommunityPost post = getById(postId);

        CommunityComment comment = new CommunityComment();
        comment.setId(UUID.randomUUID().toString());
        comment.setUserId(commenter.userId());
        comment.setDisplayName(commenter.displayName());
        comment.setUsername(commenter.username());
        comment.setText(request.text().trim());
        comment.setCreatedAt(Instant.now());

        if (post.getComments() == null) {
            post.setComments(new ArrayList<>());
        }
        post.getComments().add(comment);
        post.setUpdatedAt(Instant.now());

        CommunityPost saved = repository.save(post);
        createCommentNotificationIfNeeded(saved, commenter, comment);
        return saved;
    }

    public CommunityPost deleteComment(String postId, String commentId) {
        CommunityPost post = getById(postId);
        if (post.getComments() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community comment not found: " + commentId);
        }
        boolean removed = post.getComments().removeIf(comment -> comment.getId().equals(commentId));
        if (!removed) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community comment not found: " + commentId);
        }
        post.setUpdatedAt(Instant.now());
        return repository.save(post);
    }

    private void applyPostValues(CommunityPost post, CreateCommunityPostRequest request, IdentityClient.IdentityUserSummary author) {
        post.setUserId(author.userId());
        post.setAuthorDisplayName(author.displayName());
        post.setAuthorUsername(author.username());
        post.setAuthorType(request.authorType() != null ? request.authorType() : CommunityAuthorType.USER);
        post.setAuthorHeadline(normalizeNullable(request.authorHeadline()));
        post.setTab(request.tab() != null ? request.tab() : CommunityTab.COMMUNITY);
        post.setVisibility(request.visibility() != null ? request.visibility() : CommunityVisibility.PUBLIC);
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setImageUrl(normalizeNullable(request.imageUrl()));
        post.setCtaText(normalizeNullable(request.ctaText()));
        post.setCtaUrl(normalizeNullable(request.ctaUrl()));
        post.setTags(normalizeTags(request.tags()));
    }

    private void applyPostValues(CommunityPost post, UpdateCommunityPostRequest request, IdentityClient.IdentityUserSummary author) {
        post.setUserId(author.userId());
        post.setAuthorDisplayName(author.displayName());
        post.setAuthorUsername(author.username());
        post.setAuthorType(request.authorType() != null ? request.authorType() : CommunityAuthorType.USER);
        post.setAuthorHeadline(normalizeNullable(request.authorHeadline()));
        post.setTab(request.tab() != null ? request.tab() : CommunityTab.COMMUNITY);
        post.setVisibility(request.visibility() != null ? request.visibility() : CommunityVisibility.PUBLIC);
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setImageUrl(normalizeNullable(request.imageUrl()));
        post.setCtaText(normalizeNullable(request.ctaText()));
        post.setCtaUrl(normalizeNullable(request.ctaUrl()));
        post.setTags(normalizeTags(request.tags()));
    }

    private IdentityClient.IdentityUserSummary requireUser(String userId) {
        if (userId == null || userId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        IdentityClient.IdentityUserSummary user = identityClient.getUserSummary(userId.trim());
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }
        return user;
    }

    private void createCommentNotificationIfNeeded(
            CommunityPost post,
            IdentityClient.IdentityUserSummary commenter,
            CommunityComment comment
    ) {
        if (post.getUserId() == null || post.getUserId().equals(commenter.userId())) {
            return;
        }

        NotificationRequest notification = new NotificationRequest();
        notification.setUserId(post.getUserId());
        notification.setActorUserId(commenter.userId());
        notification.setType("COMMUNITY_COMMENT");
        notification.setTitle("New comment on your post");
        notification.setMessage(commenter.displayName() + " commented on \"" + post.getTitle() + "\": " + comment.getText());
        notification.setSourceService("community-service");
        notification.setReferenceType("COMMUNITY_POST");
        notification.setReferenceId(post.getId());
        notification.setTargetUrl("/community/posts/" + post.getId());

        try {
            notificationClient.createNotification(notification);
        } catch (Exception ex) {
            log.warn("Failed to create community comment notification for postId={} recipientUserId={}", post.getId(), post.getUserId(), ex);
        }
    }

    private List<String> normalizeTags(List<String> tags) {
        List<String> cleaned = new ArrayList<>();
        if (tags == null) {
            return cleaned;
        }
        for (String tag : tags) {
            if (tag != null && !tag.isBlank()) {
                cleaned.add(tag.trim());
            }
        }
        return cleaned;
    }

    private String normalizeNullable(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
