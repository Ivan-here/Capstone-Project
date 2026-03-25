package com.example.reviewservice.service;

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
        // 1. Validate the Rating
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        /* --- 🚨 TEMPORARY BYPASS FOR UI TESTING 🚨 ---
           Uncomment this block once your teammate finishes the Stripe/Order flow!

        // 2. Spam Prevention
        if (repository.existsByReviewerIdAndOrderIdAndTargetId(
                review.getReviewerId(), review.getOrderId(), review.getTargetId())) {
            throw new IllegalStateException("You have already reviewed this item for this order.");
        }

        // 3. Verified Purchase Check (The Feign Call)
        boolean isVerified = orderClient.verifyOrderCompletion(review.getOrderId(), review.getReviewerId());
        if (!isVerified) {
            throw new SecurityException("Cannot leave a review. Order is not completed or does not belong to you.");
        }
        ------------------------------------------------ */

        review.setCreatedAt(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());
        return repository.save(review);
    }

    public Review updateReview(String reviewId, int newRating, String newComment) {
        Review existing = repository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

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
}/*
package com.example.reviewservice.service;

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
    private final OrderClient orderClient; // Inject the Feign Client

    public Review createReview(Review review) {
        // 1. Validate the Rating
        if (review.getRating() < 1 || review.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // 2. Spam Prevention
        if (repository.existsByReviewerIdAndOrderIdAndTargetId(
                review.getReviewerId(), review.getOrderId(), review.getTargetId())) {
            throw new IllegalStateException("You have already reviewed this item for this order.");
        }

        // 3. Verified Purchase Check (The Feign Call)
        boolean isVerified = orderClient.verifyOrderCompletion(review.getOrderId(), review.getReviewerId());
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
}*/
