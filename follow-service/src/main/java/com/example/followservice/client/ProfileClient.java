package com.example.followservice.client;

import com.example.followservice.dto.UserProfileDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${clients.profile-service.url}")
public interface ProfileClient {

    // Using your InternalProfileController endpoint
    @GetMapping("/internal/profiles/{userId}")
    UserProfileDTO getProfileByUserId(@PathVariable("userId") String userId);
}