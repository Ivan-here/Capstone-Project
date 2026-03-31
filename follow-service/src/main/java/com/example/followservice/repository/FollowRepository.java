package com.example.followservice.repository;

import com.example.followservice.model.Follow;
import com.example.followservice.model.ConnectionStatus;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByFollowerIdAndStatus(String followerId, ConnectionStatus status);
    List<Follow> findByFollowingIdAndStatus(String followingId, ConnectionStatus status);
    Optional<Follow> findByFollowerIdAndFollowingId(String followerId, String followingId);
    boolean existsByFollowerIdAndFollowingIdAndStatus(String followerId, String followingId, ConnectionStatus status);

    long countByFollowerIdAndStatus(String followerId, ConnectionStatus status);
    long countByFollowingIdAndStatus(String followingId, ConnectionStatus status);
}