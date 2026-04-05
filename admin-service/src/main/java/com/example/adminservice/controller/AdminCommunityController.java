package com.example.adminservice.controller;

import com.example.adminservice.dtos.community.CommunityPost;
import com.example.adminservice.service.AdminCommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/community/posts")
@RequiredArgsConstructor
public class AdminCommunityController {

    private final AdminCommunityService adminCommunityService;

    @GetMapping
    public List<CommunityPost> getAllPosts() {
        return adminCommunityService.getAllPosts();
    }

    @GetMapping("/{id}")
    public CommunityPost getPostById(@PathVariable String id) {
        return adminCommunityService.getPostById(id);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public CommunityPost deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        return adminCommunityService.deleteComment(postId, commentId);
    }

    @DeleteMapping("/{id}")
    public void deletePost(@PathVariable String id) {
        adminCommunityService.deletePost(id);
    }
}
