package com.example.adminservice.clients;

import com.example.adminservice.dtos.profile.ProfileResponse;
import com.example.adminservice.dtos.profile.BusinessProfile;
import com.example.adminservice.dtos.profile.BusinessType;
import com.example.adminservice.dtos.profile.PersonalProfile;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(
        name = "profileClient",
        url = "${clients.profileBaseUrl}"
)
public interface ProfileServiceClient {

    @PostMapping("/internal/profiles/{userId}/verify")
    void markProfileAsVerified(@PathVariable("userId") String userId);

    @PatchMapping("/internal/profiles/{userId}/business/verified")
    BusinessProfile setBusinessVerified(
            @PathVariable("userId") String userId,
            @RequestParam("verified") boolean verified
    );

    @GetMapping("/internal/profiles/{userId}")
    ProfileResponse getProfileInternal(@PathVariable("userId") String userId);

    @GetMapping("/internal/profiles/{userId}/personal")
    PersonalProfile getPersonalProfileInternal(@PathVariable("userId") String userId);

    @GetMapping("/internal/profiles/{userId}/business")
    BusinessProfile getBusinessProfileInternal(@PathVariable("userId") String userId);

    @GetMapping("/internal/profiles/personal")
    List<PersonalProfile> getAllPersonalProfiles();

    @GetMapping("/internal/profiles/business")
    List<BusinessProfile> getAllBusinessProfiles();

    @GetMapping("/internal/profiles/business/type/{businessType}")
    List<BusinessProfile> getBusinessProfilesByType(
            @PathVariable("businessType") BusinessType businessType
    );

    @GetMapping("/internal/profiles/business/verified/{verified}")
    List<BusinessProfile> getBusinessProfilesByVerified(
            @PathVariable("verified") boolean verified
    );

    @DeleteMapping("/internal/profiles/{userId}/personal")
    void deletePersonalProfileInternal(@PathVariable("userId") String userId);

    @DeleteMapping("/internal/profiles/{userId}/business")
    void deleteBusinessProfileInternal(@PathVariable("userId") String userId);

    @DeleteMapping("/profiles/me/settings/internal/account/{userId}")
    void deleteAccountInternal(@PathVariable("userId") String userId);
}
