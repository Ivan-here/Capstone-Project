package com.example.adminservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "admin_action_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminActionLog {

    @Id
    private String id;

    private String adminUserId;     // user id of the admin
    private String actionType;      // APPROVE_VERIFICATION, DELETE_LISTING, BAN_USER, etc.
    private String targetType;      // USER, LISTING, PROFILE, ORDER, VERIFICATION
    private String targetId;        // id of the affected thing
    private String description;     // optional description

    private Instant createdAt;
}