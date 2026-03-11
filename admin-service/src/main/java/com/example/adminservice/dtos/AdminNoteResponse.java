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
public class AdminNoteResponse {
    private String id;
    private String adminUserId;
    private String targetType;
    private String targetId;
    private String note;
    private Instant createdAt;
    private Instant updatedAt;
}