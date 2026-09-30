package com.example.reviewservice.service;

import com.locally.security.Access;

import com.example.reviewservice.client.OrderClient;
import com.example.reviewservice.model.Review;
import com.example.reviewservice.model.TargetType;
import com.example.reviewservice.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository repository;
    private final OrderClient orderClient;

    public Review createReview(Review review) {
        Access.owner(review.getReviewerId());
        review.setId(null);
        if (review.getTargetType() == null || review.getOrderId() == null || review.getTargetId() == null || (review.getComment() != null && review.getComment().length() > 4000)) throw new IllegalArgumentException("Invalid review");
        // 1. Validate the Rating
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Reject duplicate and unverified reviews.
        // 2. Spam Prevention
        if (repository.existsByReviewerIdAndOrderIdAndTargetId(
                review.getReviewerId(), review.getOrderId(), review.getTargetId())) {
            throw new IllegalStateException("You have already reviewed this item for this order.");
        }

        // 3. Verified Purchase Check (The Feign Call)
        boolean isVerified = orderClient.verifyOrderCompletion(review.getOrderId(), review.getReviewerId(), review.getTargetId(), review.getTargetType().name());
        if (!isVerified) {
            throw new SecurityException("Cannot leave a review. Order is not completed or does not belong to you.");
        }


        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    public Review updateReview(String reviewId, int newRating, String newComment) {
        Review existing = repository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        Access.owner(existing.getReviewerId());
        if (newRating < 1 || newRating > 5) throw new IllegalArgumentException("Rating must be between 1 and 5");
        if (newComment != null && newComment.length() > 4000) throw new IllegalArgumentException("Comment too long");
        existing.setRating(newRating);
        existing.setComment(newComment);
        existing.setUpdatedAt(LocalDateTime.now());

        return repository.save(existing);
    }

    public List<Review> getReviewsForTarget(String targetId, TargetType type) {
        return repository.findByTargetIdAndTargetType(targetId, type);
    }

    public List<Review> getReviewsByReviewer(String reviewerId) {
        return repository.findByReviewerId(reviewerId);
    }
}
