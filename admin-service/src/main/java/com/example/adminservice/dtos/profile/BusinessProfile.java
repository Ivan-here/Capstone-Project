package com.example.adminservice.dtos.profile;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Document("business_profiles")
public class BusinessProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private BusinessType businessType;

    private String businessName;
    private String address;

    private String email;

    private boolean verified = false;

    private String hours;
    private String description;
    private String serviceArea;
    private String eligibilityNotes;

    private Instant createdAt;
    private Instant updatedAt;
}
