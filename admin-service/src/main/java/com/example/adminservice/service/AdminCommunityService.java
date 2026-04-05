package com.example.adminservice.service;

import com.example.adminservice.clients.CommunityServiceClient;
import com.example.adminservice.dtos.community.CommunityComment;
import com.example.adminservice.dtos.community.CommunityPost;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminCommunityService {

    private final CommunityServiceClient communityServiceClient;
    private final AdminManagementService adminManagementService;

    public List<CommunityPost> getAllPosts() {
        return communityServiceClient.getAllPosts();
    }

    public CommunityPost getPostById(String id) {
        return communityServiceClient.getPostById(id);
    }

    public CommunityPost deleteComment(String postId, String commentId) {
        CommunityPost existing = communityServiceClient.getPostById(postId);
        CommunityComment comment = existing.getComments() == null
                ? null
                : existing.getComments().stream().filter(item -> commentId.equals(item.getId())).findFirst().orElse(null);

        if (comment == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Community comment not found: " + commentId);
        }

        CommunityPost updated = communityServiceClient.deleteComment(postId, commentId);

        adminManagementService.logAudit(
                "COMMUNITY_COMMENT_DELETED",
                "COMMUNITY_COMMENT",
                commentId,
                "ADMIN",
                "Admin deleted community comment",
                Map.of(
                        "postId", postId,
                        "commentId", commentId,
                        "userId", comment.getUserId(),
                        "commentPreview", comment.getText() == null ? "" : comment.getText()
                )
        );

        return updated;
    }

    public void deletePost(String id) {
        CommunityPost existing = communityServiceClient.getPostById(id);
        communityServiceClient.deletePost(id);

        adminManagementService.logAudit(
                "COMMUNITY_POST_DELETED",
                "COMMUNITY_POST",
                id,
                "ADMIN",
                "Admin deleted community post",
                Map.of(
                        "postId", id,
                        "userId", existing.getUserId(),
                        "title", existing.getTitle()
                )
        );
    }
}
