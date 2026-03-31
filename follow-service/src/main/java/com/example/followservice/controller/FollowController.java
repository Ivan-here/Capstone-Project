package com.example.followservice.controller;

import com.example.followservice.dto.FollowResponseDTO;
import com.example.followservice.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor // Autowires the FollowService automatically
public class FollowController {

    private final FollowService followService;

    @PostMapping("/{targetId}")
    public ResponseEntity<Void> follow(@RequestHeader("userId") String userId, @PathVariable String targetId) {
        followService.followUser(userId, targetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{targetId}")
    public ResponseEntity<Void> unfollow(@RequestHeader("userId") String userId, @PathVariable String targetId) {
        followService.unfollowUser(userId, targetId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/remove/{followerId}")
    public ResponseEntity<Void> removeFollower(@RequestHeader("userId") String userId, @PathVariable String followerId) {
        followService.unfollowUser(followerId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{targetId}/block")
    public ResponseEntity<Void> block(@RequestHeader("userId") String userId, @PathVariable String targetId) {
        followService.blockUser(userId, targetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/stats")
    public ResponseEntity<Map<String, Long>> getStats(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getStats(userId));
    }

    @GetMapping("/{userId}/followers")
    public ResponseEntity<List<FollowResponseDTO>> getFollowers(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<List<FollowResponseDTO>> getFollowing(@PathVariable String userId) {
        return ResponseEntity.ok(followService.getFollowing(userId));
    }
}