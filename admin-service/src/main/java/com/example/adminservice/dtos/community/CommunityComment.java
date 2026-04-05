package com.example.adminservice.dtos.community;

import lombok.Data;

import java.time.Instant;

@Data
public class CommunityComment {
    private String id;
    private String userId;
    private String displayName;
    private String username;
    private String text;
    private Instant createdAt;
}
