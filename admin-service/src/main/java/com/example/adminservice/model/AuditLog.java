package com.example.adminservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Document(collection = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    private String id;

    private String eventType;       // USER_VERIFIED, LISTING_REMOVED, ORDER_CANCELLED, etc.
    private String entityType;      // USER, LISTING, ORDER, RESERVATION, VERIFICATION
    private String entityId;        // related entity id
    private String performedBy;     // admin user id or system
    private String description;     // optional summary

    private Map<String, Object> metadata; // optional extra structured info

    private Instant createdAt;
}