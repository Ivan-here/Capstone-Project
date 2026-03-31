package com.example.followservice.service;

import com.example.followservice.client.ProfileClient;
import com.example.followservice.dto.FlatUserDTO;
import com.example.followservice.dto.FollowResponseDTO;
import com.example.followservice.dto.UserProfileDTO;
import com.example.followservice.model.ConnectionStatus;
import com.example.followservice.model.Follow;
import com.example.followservice.repository.FollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;
    private final ProfileClient profileClient;

    public void followUser(String followerId, String followingId) {
        if (followerId.equals(followingId)) throw new IllegalArgumentException("Cannot follow yourself");

        Optional<Follow> existing = followRepository.findByFollowerIdAndFollowingId(followerId, followingId);
        if (existing.isPresent()) {
            if (existing.get().getStatus() == ConnectionStatus.BLOCKED) {
                throw new IllegalStateException("You are blocked from following this user.");
            }
            return;
        }
        followRepository.save(new Follow(followerId, followingId));
    }

    public void unfollowUser(String followerId, String followingId) {
        followRepository.findByFollowerIdAndFollowingId(followerId, followingId)
                .ifPresent(followRepository::delete);
    }

    public void blockUser(String blockerId, String blockedId) {
        unfollowUser(blockedId, blockerId);

        Follow blockRecord = followRepository.findByFollowerIdAndFollowingId(blockerId, blockedId)
                .orElse(new Follow(blockerId, blockedId));

        blockRecord.setStatus(ConnectionStatus.BLOCKED);
        followRepository.save(blockRecord);
    }

    public Map<String, Long> getStats(String userId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("followers", followRepository.countByFollowingIdAndStatus(userId, ConnectionStatus.ACTIVE));
        stats.put("following", followRepository.countByFollowerIdAndStatus(userId, ConnectionStatus.ACTIVE));
        return stats;
    }

    // ... inside FollowService.java ...

    public List<FollowResponseDTO> getFollowers(String userId) {
        List<Follow> follows = followRepository.findByFollowingIdAndStatus(userId, ConnectionStatus.ACTIVE);

        return follows.stream().map(f -> {
            UserProfileDTO rawUser = fetchProfileSafely(f.getFollowerId());
            boolean isMutual = followRepository.existsByFollowerIdAndFollowingIdAndStatus(userId, f.getFollowerId(), ConnectionStatus.ACTIVE);

            // Create a flat object for React
            FlatUserDTO flatUser = new FlatUserDTO(rawUser.extractId(), rawUser.extractDisplayName(), rawUser.extractRole(), rawUser.extractAvatarUrl());

            return new FollowResponseDTO(f.getId(), flatUser, isMutual, f.getCreatedAt());
        }).toList();
    }

    public List<FollowResponseDTO> getFollowing(String userId) {
        List<Follow> follows = followRepository.findByFollowerIdAndStatus(userId, ConnectionStatus.ACTIVE);

        return follows.stream().map(f -> {
            UserProfileDTO rawUser = fetchProfileSafely(f.getFollowingId());
            boolean isMutual = followRepository.existsByFollowerIdAndFollowingIdAndStatus(f.getFollowingId(), userId, ConnectionStatus.ACTIVE);

            // Create a flat object for React
            FlatUserDTO flatUser = new FlatUserDTO(rawUser.extractId(), rawUser.extractDisplayName(), rawUser.extractRole(), rawUser.extractAvatarUrl());

            return new FollowResponseDTO(f.getId(), flatUser, isMutual, f.getCreatedAt());
        }).toList();
    }

    // Helper method
    private UserProfileDTO fetchProfileSafely(String targetUserId) {
        try {
            return profileClient.getProfileByUserId(targetUserId);
        } catch (Exception e) {
            System.err.println("Failed to fetch profile: " + targetUserId);
            UserProfileDTO fallback = new UserProfileDTO();
            fallback.setUserId(targetUserId); // At least give React the ID!
            return fallback;
        }
    }
}