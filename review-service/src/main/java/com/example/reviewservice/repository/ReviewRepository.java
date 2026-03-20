package com.example.reviewservice.repository;

import com.example.reviewservice.model.Review;
import com.example.reviewservice.model.TargetType;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends MongoRepository<Review, String> {

    List<Review> findByTargetIdAndTargetType(String targetId, TargetType targetType);

    List<Review> findByReviewerId(String reviewerId);

    // Spam prevention: 1 review per user, per order, per target
    boolean existsByReviewerIdAndOrderIdAndTargetId(String reviewerId, String orderId, String targetId);
}