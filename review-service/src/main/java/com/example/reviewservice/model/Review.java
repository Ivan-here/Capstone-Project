package com.example.reviewservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reviews")
public class Review {

    @Id
    private String id;

    // The Trust Factor
    private String orderId;

    // Reviewer Info
    private String reviewerId;
    private boolean isAnonymous;

    // Target Info
    private String targetId;
    private TargetType targetType;

    // Content
    private int rating; // 1 to 5
    private String comment;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}