package com.example.profileservice.profiles.service;

import com.example.profileservice.profiles.dto.BusinessProfileRequest;
import com.example.profileservice.profiles.dto.PersonalProfileRequest;
import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.PersonalProfile;

public interface ProfileService {

    ProfileResponse getMe(String userId);

    PersonalProfile upsertPersonal(String userId, PersonalProfileRequest req);

    BusinessProfile upsertBusiness(String userId, BusinessProfileRequest req);

    void deleteBusiness(String userId);
}