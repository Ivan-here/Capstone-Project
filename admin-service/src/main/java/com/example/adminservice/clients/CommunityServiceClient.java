package com.example.adminservice.clients;

import com.example.adminservice.dtos.community.CommunityPost;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "communityClient",
        url = "${clients.communityBaseUrl}"
)
public interface CommunityServiceClient {

    @GetMapping("/api/community/posts")
    List<CommunityPost> getAllPosts();

    @GetMapping("/api/community/posts/{id}")
    CommunityPost getPostById(@PathVariable("id") String id);

    @DeleteMapping("/api/community/posts/{postId}/comments/{commentId}")
    CommunityPost deleteComment(
            @PathVariable("postId") String postId,
            @PathVariable("commentId") String commentId
    );

    @DeleteMapping("/api/community/posts/{id}")
    void deletePost(@PathVariable("id") String id);
}
