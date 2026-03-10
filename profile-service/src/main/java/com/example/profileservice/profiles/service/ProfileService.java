package com.example.profileservice.profiles.service;

import com.example.profileservice.profiles.dto.BusinessProfileRequest;
import com.example.profileservice.profiles.dto.PersonalProfileRequest;
import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import com.example.profileservice.profiles.model.PersonalProfile;

import java.util.List;

public interface ProfileService {

    ProfileResponse getMe(String userId);
    ProfileResponse getProfileByUserId(String userId);

    PersonalProfile getPersonalByUserId(String userId);
    BusinessProfile getBusinessByUserId(String userId);

    List<PersonalProfile> getAllPersonalProfiles();
    List<BusinessProfile> getAllBusinessProfiles();
    List<BusinessProfile> getBusinessProfilesByType(BusinessType businessType);
    List<BusinessProfile> getBusinessProfilesByVerified(boolean verified);

    PersonalProfile upsertPersonal(String userId, PersonalProfileRequest req);
    BusinessProfile upsertBusiness(String userId, BusinessProfileRequest req);

    BusinessProfile setBusinessVerified(String userId, boolean verified);
    void verifyUser(String userId);

    void deletePersonal(String userId);
    void deleteBusiness(String userId);
}