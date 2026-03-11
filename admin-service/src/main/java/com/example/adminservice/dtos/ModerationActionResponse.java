package com.example.adminservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModerationActionResponse {
    private String id;
    private String adminUserId;
    private String targetType;
    private String targetId;
    private String action;
    private String reason;
    private String notes;
    private Instant createdAt;
}