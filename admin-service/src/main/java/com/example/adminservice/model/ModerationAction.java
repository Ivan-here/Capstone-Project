package com.example.adminservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "moderation_actions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ModerationAction {

    @Id
    private String id;

    private String adminUserId;     // admin who made the moderation decision
    private String targetType;      // USER, LISTING, PROFILE, VERIFICATION
    private String targetId;        // moderated entity id
    private String action;          // HIDE, REMOVE, WARN, SUSPEND, REJECT, RESTORE
    private String reason;          // why action was taken
    private String notes;           // optional extra notes

    private Instant createdAt;
}