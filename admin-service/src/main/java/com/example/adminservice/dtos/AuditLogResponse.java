package com.example.adminservice.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponse {
    private String id;
    private String eventType;
    private String entityType;
    private String entityId;
    private String performedBy;
    private String description;
    private Map<String, Object> metadata;
    private Instant createdAt;
}