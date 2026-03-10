package com.example.profileservice.profiles.controller;

import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import com.example.profileservice.profiles.model.PersonalProfile;
import com.example.profileservice.profiles.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/internal/profiles")
@RequiredArgsConstructor
@Slf4j
public class InternalProfileController {

    private final ProfileService profileService;

    @PostMapping("/{userId}/verify")
    public void markProfileAsVerified(@PathVariable String userId) {
        log.info("Internal Request: Marking user {} as verified.", userId);
        profileService.verifyUser(userId);
    }

    // ----------------------------------------------------------------
    // 2. Used by LISTING SERVICE (This is the missing part!)
    // ----------------------------------------------------------------
    @GetMapping("/{userId}")
    public ProfileResponse getProfileInternal(@PathVariable String userId) {
        log.info("Internal Request: Fetching profile details for user {}", userId);
        // We reuse the existing service method that fetches Personal + Business profile
        return profileService.getMe(userId);
    }

    @PatchMapping("/{userId}/business/verified")
    public BusinessProfile setBusinessVerified(
            @PathVariable String userId,
            @RequestParam boolean verified
    ) {
        log.info("Internal Request: Setting verified={} for user {}", verified, userId);
        return profileService.setBusinessVerified(userId, verified);
    }

    @GetMapping("/{userId}/personal")
    public PersonalProfile getPersonalProfileInternal(@PathVariable String userId) {
        log.info("Internal Request: Fetching personal profile for user {}", userId);
        return profileService.getPersonalByUserId(userId);
    }

    @GetMapping("/{userId}/business")
    public BusinessProfile getBusinessProfileInternal(@PathVariable String userId) {
        log.info("Internal Request: Fetching business profile for user {}", userId);
        return profileService.getBusinessByUserId(userId);
    }

    @GetMapping("/personal")
    public List<PersonalProfile> getAllPersonalProfiles() {
        log.info("Internal Request: Fetching all personal profiles");
        return profileService.getAllPersonalProfiles();
    }

    @GetMapping("/business")
    public List<BusinessProfile> getAllBusinessProfiles() {
        log.info("Internal Request: Fetching all business profiles");
        return profileService.getAllBusinessProfiles();
    }

    @GetMapping("/business/type/{businessType}")
    public List<BusinessProfile> getBusinessProfilesByType(@PathVariable BusinessType businessType) {
        log.info("Internal Request: Fetching business profiles by type {}", businessType);
        return profileService.getBusinessProfilesByType(businessType);
    }

    @GetMapping("/business/verified/{verified}")
    public List<BusinessProfile> getBusinessProfilesByVerified(@PathVariable boolean verified) {
        log.info("Internal Request: Fetching business profiles by verified={}", verified);
        return profileService.getBusinessProfilesByVerified(verified);
    }

    @DeleteMapping("/{userId}/personal")
    public void deletePersonalProfileInternal(@PathVariable String userId) {
        log.info("Internal Request: Deleting personal profile for user {}", userId);
        profileService.deletePersonal(userId);
    }

    @DeleteMapping("/{userId}/business")
    public void deleteBusinessProfileInternal(@PathVariable String userId) {
        log.info("Internal Request: Deleting business profile for user {}", userId);
        profileService.deleteBusiness(userId);
    }
}