package com.example.profileservice.profiles.dto;

import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.PersonalProfile;

public record ProfileResponse(
        String userId,
        PersonalProfile personalProfile,
        BusinessProfile businessProfile
) {}