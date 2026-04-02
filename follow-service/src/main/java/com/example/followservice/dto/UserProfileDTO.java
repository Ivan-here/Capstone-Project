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
        private String username;
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

    public String extractId() {
        return userId;
    }

    public String extractDisplayName() {
        String businessName = normalize(businessProfile != null ? businessProfile.getBusinessName() : null);
        if (businessName != null) return businessName;

        String displayName = normalize(personalProfile != null ? personalProfile.getDisplayName() : null);
        if (displayName != null) return displayName;

        String username = normalize(personalProfile != null ? personalProfile.getUsername() : null);
        if (username != null) return "@" + username;

        String firstName = normalize(personalProfile != null ? personalProfile.getFirstName() : null);
        if (firstName != null) return firstName;

        return "Someone";
    }

    public String extractUsername() {
        return normalize(personalProfile != null ? personalProfile.getUsername() : null);
    }

    public String extractRole() {
        String businessType = normalize(businessProfile != null ? businessProfile.getBusinessType() : null);
        if (businessType != null) return businessType;

        String role = normalize(personalProfile != null ? personalProfile.getRole() : null);
        if (role != null) return role;

        return "User";
    }

    public String extractAvatarUrl() {
        String businessAvatar = normalize(businessProfile != null ? businessProfile.getAvatarUrl() : null);
        if (businessAvatar != null) return businessAvatar;

        String personalAvatar = normalize(personalProfile != null ? personalProfile.getAvatarUrl() : null);
        if (personalAvatar != null) return personalAvatar;

        return null;
    }

    private String normalize(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
