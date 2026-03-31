package com.example.followservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfileDTO {
    private String userId;
    private PersonalProfile personalProfile;
    private BusinessProfile businessProfile;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PersonalProfile {
        private String displayName;
        private String firstName;
        private String role;
        private String avatarUrl;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class BusinessProfile {
        private String businessName;
        private String businessType;
        private String avatarUrl;
    }

    // Helper methods to flatten the data for React!
    public String extractId() {
        return userId;
    }

    public String extractDisplayName() {
        if (businessProfile != null && businessProfile.getBusinessName() != null) return businessProfile.getBusinessName();
        if (personalProfile != null && personalProfile.getDisplayName() != null) return personalProfile.getDisplayName();
        if (personalProfile != null && personalProfile.getFirstName() != null) return personalProfile.getFirstName();
        return "Unknown User";
    }

    public String extractRole() {
        if (businessProfile != null && businessProfile.getBusinessType() != null) return businessProfile.getBusinessType();
        if (personalProfile != null && personalProfile.getRole() != null) return personalProfile.getRole();
        return "User";
    }

    public String extractAvatarUrl() {
        if (businessProfile != null && businessProfile.getAvatarUrl() != null) return businessProfile.getAvatarUrl();
        if (personalProfile != null && personalProfile.getAvatarUrl() != null) return personalProfile.getAvatarUrl();
        return null;
    }
}