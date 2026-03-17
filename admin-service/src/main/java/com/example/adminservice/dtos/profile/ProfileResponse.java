package com.example.adminservice.dtos.profile;

public record ProfileResponse(
        String userId,
        PersonalProfile personalProfile,
        BusinessProfile businessProfile
) {}