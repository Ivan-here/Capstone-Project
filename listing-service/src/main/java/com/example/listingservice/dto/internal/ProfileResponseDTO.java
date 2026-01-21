package com.example.listingservice.dto.internal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

// 1. Map the outer JSON object
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileResponseDTO(
        String userId,
        @JsonProperty("businessProfile") BusinessProfileDTO businessProfile
) {
    // --- HELPER METHODS (Use these in ListingService!) ---

    public boolean isVerified() {
        return businessProfile != null && businessProfile.Verified();
    }

    public String getBusinessName() {
        return businessProfile != null ? businessProfile.businessName() : null;
    }

    public String getPickupLocation() {
        return businessProfile != null ? businessProfile.address() : null;
    }

    public String getBusinessType() {
        return businessProfile != null ? businessProfile.businessType() : null;
    }
}

// 2. Map the inner JSON object
@JsonIgnoreProperties(ignoreUnknown = true)
record BusinessProfileDTO(
        String businessName,
        String address,
        String businessType, // <--- Added this back!
         boolean Verified // Maps JSON "verified" to Java "isVerified"
) {}