package com.example.adminservice.service;

import com.example.adminservice.clients.ProfileServiceClient;
import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import com.example.profileservice.profiles.model.PersonalProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminProfileService {

    private final ProfileServiceClient profileServiceClient;

    public ProfileResponse getProfileByUserId(String userId) {
        return profileServiceClient.getProfileInternal(userId);
    }

    public PersonalProfile getPersonalProfile(String userId) {
        return profileServiceClient.getPersonalProfileInternal(userId);
    }

    public BusinessProfile getBusinessProfile(String userId) {
        return profileServiceClient.getBusinessProfileInternal(userId);
    }

    public List<PersonalProfile> getAllPersonalProfiles() {
        return profileServiceClient.getAllPersonalProfiles();
    }

    public List<BusinessProfile> getAllBusinessProfiles() {
        return profileServiceClient.getAllBusinessProfiles();
    }

    public List<BusinessProfile> getBusinessProfilesByType(String businessType) {
        return profileServiceClient.getBusinessProfilesByType(
                BusinessType.valueOf(businessType.toUpperCase())
        );
    }

    public List<BusinessProfile> getBusinessProfilesByVerified(boolean verified) {
        return profileServiceClient.getBusinessProfilesByVerified(verified);
    }

    public BusinessProfile setBusinessVerified(String userId, boolean verified) {
        return profileServiceClient.setBusinessVerified(userId, verified);
    }

    public void verifyUser(String userId) {
        profileServiceClient.markProfileAsVerified(userId);
    }

    public void deletePersonalProfile(String userId) {
        profileServiceClient.deletePersonalProfileInternal(userId);
    }

    public void deleteBusinessProfile(String userId) {
        profileServiceClient.deleteBusinessProfileInternal(userId);
    }
}