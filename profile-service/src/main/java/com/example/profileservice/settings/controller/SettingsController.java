package com.example.profileservice.settings.controller;

import com.example.profileservice.settings.dto.SettingsOverviewResponse;
import com.example.profileservice.settings.dto.VerificationRecordResponse;
import com.example.profileservice.settings.service.SettingsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/profiles/me/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final SettingsService settingsService;

    @GetMapping
    public SettingsOverviewResponse getOverview(Authentication authentication) {
        String userId = userId(authentication);
        return settingsService.getOverview(userId);
    }

    @GetMapping("/verification")
    public VerificationRecordResponse getBusinessVerification(Authentication authentication) {
        String userId = userId(authentication);
        return settingsService.getBusinessVerification(userId);
    }

    @PostMapping(value = "/verification/resubmit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public VerificationRecordResponse resubmitVerification(
            Authentication authentication,
            @RequestParam(value = "type", required = false) String type,
            @RequestPart("document") MultipartFile document
    ) {
        String userId = userId(authentication);
        return settingsService.resubmitVerification(userId, type, document);
    }

    @DeleteMapping("/profile")
    public void deletePersonalProfile(Authentication authentication) {
        String userId = userId(authentication);
        settingsService.deletePersonalProfile(userId);
    }

    @DeleteMapping("/business-profile")
    public void deleteBusinessProfile(Authentication authentication) {
        String userId = userId(authentication);
        settingsService.deleteBusinessProfile(userId);
    }

    @DeleteMapping("/account")
    public void deleteAccount(Authentication authentication) {
        String userId = userId(authentication);
        settingsService.deleteAccount(userId);
    }

    private String userId(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            throw new IllegalStateException("Unauthenticated request (missing userId)");
        }
        return authentication.getName();
    }
}
