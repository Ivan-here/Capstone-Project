package com.example.profileservice.profiles.controller;

import com.example.profileservice.profiles.dto.BusinessProfileRequest;
import com.example.profileservice.profiles.dto.PersonalProfileRequest;
import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.PersonalProfile;
import com.example.profileservice.profiles.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    /**
     * JwtAuthFilter sets Authentication principal to userId (JWT sub).
     */
    private String userId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            //!endpoints require authentication anyway, but just to be sure
            throw new IllegalStateException("Unauthenticated request (missing userId)");
        }
        return authentication.getName();
    }

    @GetMapping("/me")
    public ProfileResponse getMyProfiles(Authentication authentication) {
        String uid = userId(authentication);
        log.info("GET /profiles/me userId={}", uid);
        return profileService.getMe(uid);
    }

    @PutMapping("/me/personal")
    public PersonalProfile upsertPersonalProfile(
            Authentication authentication,
            @Valid @RequestBody PersonalProfileRequest request
    ) {
        String uid = userId(authentication);
        log.info("PUT /profiles/me/personal userId={}", uid);
        return profileService.upsertPersonal(uid, request);
    }

    @PutMapping("/me/business")
    public BusinessProfile upsertBusinessProfile(
            Authentication authentication,
            @Valid @RequestBody BusinessProfileRequest request
    ) {
        String uid = userId(authentication);
        log.info("PUT /profiles/me/business userId={}, type={}", uid, request.businessType());
        return profileService.upsertBusiness(uid, request);
    }

    @DeleteMapping("/me/business")
    public void deleteBusinessProfile(Authentication authentication) {
        String uid = userId(authentication);
        log.info("DELETE /profiles/me/business userId={}", uid);
        profileService.deleteBusiness(uid);
    }

    // Add this to ProfileController.java
    @GetMapping("/{userId}")
    public ProfileResponse getProfileById(@PathVariable String userId) {
        log.info("GET /profiles/{}", userId);
        return profileService.getMe(userId);
    }
}