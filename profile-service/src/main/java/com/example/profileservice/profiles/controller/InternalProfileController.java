package com.example.profileservice.profiles.controller;

import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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

}