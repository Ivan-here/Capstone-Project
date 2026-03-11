package com.example.adminservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "admin_notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNote {

    @Id
    private String id;

    private String adminUserId;     // who wrote the note
    private String targetType;      // USER, LISTING, PROFILE, ORDER, VERIFICATION
    private String targetId;        // entity id the note belongs to
    private String note;            // actual note text

    private Instant createdAt;
    private Instant updatedAt;
}
