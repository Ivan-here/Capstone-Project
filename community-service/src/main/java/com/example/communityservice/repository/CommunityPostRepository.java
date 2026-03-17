package com.example.communityservice.repository;

import com.example.communityservice.model.CommunityPost;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommunityPostRepository extends MongoRepository<CommunityPost, String> {
    List<CommunityPost> findAllByOrderByCreatedAtDesc();
    List<CommunityPost> findByUserIdOrderByCreatedAtDesc(String userId);
}
