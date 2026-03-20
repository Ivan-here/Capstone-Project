package com.example.reviewservice.controller;

import com.example.reviewservice.model.Review;
import com.example.reviewservice.model.TargetType;
import com.example.reviewservice.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // CREATE a new review
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Review addReview(@RequestBody Review review) {
        return reviewService.createReview(review);
    }

    // UPDATE an existing review (e.g., changing 1-star to 4-star)
    @PutMapping("/{id}")
    public Review updateReview(
            @PathVariable String id,
            @RequestParam int rating,
            @RequestParam String comment) {
        return reviewService.updateReview(id, rating, comment);
    }

    // GET all reviews for a specific Product or Seller
    @GetMapping("/{targetType}/{targetId}")
    public List<Review> getTargetReviews(
            @PathVariable TargetType targetType,
            @PathVariable String targetId) {
        return reviewService.getReviewsForTarget(targetId, targetType);
    }

    // GET the average rating (e.g., for the Browse grid)
    @GetMapping("/{targetType}/{targetId}/average")
    public Map<String, Double> getAverageRating(
            @PathVariable TargetType targetType,
            @PathVariable String targetId) {

        List<Review> reviews = reviewService.getReviewsForTarget(targetId, targetType);
        double avg = 0.0;

        if (!reviews.isEmpty()) {
            double sum = reviews.stream().mapToInt(Review::getRating).sum();
            avg = Math.round((sum / reviews.size()) * 10.0) / 10.0;
        }

        Map<String, Double> response = new HashMap<>();
        response.put("averageRating", avg);
        response.put("totalReviews", (double) reviews.size());
        return response;
    }

    // GET all reviews written BY a specific user
    @GetMapping("/reviewer/{reviewerId}")
    public List<Review> getReviewsByReviewer(@PathVariable String reviewerId) {
        return reviewService.getReviewsByReviewer(reviewerId);
    }
}