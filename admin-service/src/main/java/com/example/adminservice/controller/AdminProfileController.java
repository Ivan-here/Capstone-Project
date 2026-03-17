package com.example.adminservice.controller;

import com.example.adminservice.dtos.profile.ProfileResponse;
import com.example.adminservice.dtos.profile.BusinessProfile;
import com.example.adminservice.dtos.profile.PersonalProfile;
import com.example.adminservice.service.AdminProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/profiles")
@RequiredArgsConstructor
public class AdminProfileController {

    private final AdminProfileService adminProfileService;

    @GetMapping("/{userId}")
    public ProfileResponse getProfileByUserId(@PathVariable String userId) {
        return adminProfileService.getProfileByUserId(userId);
    }

    @GetMapping("/{userId}/personal")
    public PersonalProfile getPersonalProfile(@PathVariable String userId) {
        return adminProfileService.getPersonalProfile(userId);
    }

    @GetMapping("/{userId}/business")
    public BusinessProfile getBusinessProfile(@PathVariable String userId) {
        return adminProfileService.getBusinessProfile(userId);
    }

    @GetMapping("/personal")
    public List<PersonalProfile> getAllPersonalProfiles() {
        return adminProfileService.getAllPersonalProfiles();
    }

    @GetMapping("/business")
    public List<BusinessProfile> getAllBusinessProfiles() {
        return adminProfileService.getAllBusinessProfiles();
    }

    @GetMapping("/business/type/{businessType}")
    public List<BusinessProfile> getBusinessProfilesByType(@PathVariable String businessType) {
        return adminProfileService.getBusinessProfilesByType(businessType);
    }

    @GetMapping("/business/verified/{verified}")
    public List<BusinessProfile> getBusinessProfilesByVerified(@PathVariable boolean verified) {
        return adminProfileService.getBusinessProfilesByVerified(verified);
    }

    @PatchMapping("/{userId}/business/verified")
    public BusinessProfile setBusinessVerified(
            @PathVariable String userId,
            @RequestParam boolean verified
    ) {
        return adminProfileService.setBusinessVerified(userId, verified);
    }

    @PostMapping("/{userId}/verify")
    public void verifyUser(@PathVariable String userId) {
        adminProfileService.verifyUser(userId);
    }

    @DeleteMapping("/{userId}/personal")
    public void deletePersonalProfile(@PathVariable String userId) {
        adminProfileService.deletePersonalProfile(userId);
    }

    @DeleteMapping("/{userId}/business")
    public void deleteBusinessProfile(@PathVariable String userId) {
        adminProfileService.deleteBusinessProfile(userId);
    }
}