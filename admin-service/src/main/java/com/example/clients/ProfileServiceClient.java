package com.example.clients;

import com.example.profileservice.profiles.dto.ProfileResponse;
import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import com.example.profileservice.profiles.model.PersonalProfile;
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
}
