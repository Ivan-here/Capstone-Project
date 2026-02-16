package com.example.profileservice.profiles.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    // ADD THIS FIELD
    private boolean isVerified = false;

    //type-specific
    private String hours;               // restaurant
    private String description;  // restaurant/farmer
    private String serviceArea;         // NGO
    private String eligibilityNotes;    // NGO

    private Instant createdAt;
    private Instant updatedAt;

    public void setEmail(@NotBlank(message = "email is required") @Size(max = 120, message = "email max 120 chars") String email) {
    }
}