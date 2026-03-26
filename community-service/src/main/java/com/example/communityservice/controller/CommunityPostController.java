package com.example.communityservice.controller;

import com.example.communityservice.dto.CreateCommunityCommentRequest;
import com.example.communityservice.dto.CreateCommunityPostRequest;
import com.example.communityservice.dto.UpdateCommunityPostRequest;
import com.example.communityservice.dto.UpdateCommunityReactionRequest;
import com.example.communityservice.model.*;
import com.example.communityservice.service.CommunityPostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community/posts")
public class CommunityPostController {

    private final CommunityPostService service;

    public CommunityPostController(CommunityPostService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommunityPost create(@Valid @RequestBody CreateCommunityPostRequest request) {
        return service.create(request);
    }

    @GetMapping
    public List<CommunityPost> listAll(
            @RequestParam(required = false) CommunityTab tab,
            @RequestParam(required = false) CommunityVisibility visibility,
            @RequestParam(required = false) CommunityAuthorType authorType,
            @RequestParam(required = false) String userId
    ) {
        return service.listAll(tab, visibility, authorType, userId);
    }

    @GetMapping("/user/{userId}")
    public List<CommunityPost> listByUser(@PathVariable String userId) {
        return service.listByUser(userId);
    }

    @GetMapping("/{id}")
    public CommunityPost getById(@PathVariable String id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public CommunityPost update(
            @PathVariable String id,
            @Valid @RequestBody UpdateCommunityPostRequest request
    ) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        service.delete(id);
    }

    @GetMapping("/{postId}/comments")
    public List<CommunityComment> listComments(@PathVariable String postId) {
        return service.listComments(postId);
    }

    @PostMapping("/{postId}/comments")
    public CommunityPost addComment(
            @PathVariable String postId,
            @Valid @RequestBody CreateCommunityCommentRequest request
    ) {
        return service.addComment(postId, request);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public CommunityPost deleteComment(
            @PathVariable String postId,
            @PathVariable String commentId
    ) {
        return service.deleteComment(postId, commentId);
    }

    @PatchMapping("/{postId}/reactions")
    public CommunityPost updateReaction(
            @PathVariable String postId,
            @Valid @RequestBody UpdateCommunityReactionRequest request
    ) {
        return service.updateReaction(postId, request);
    }
}
