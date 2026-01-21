package com.example.listingservice.client;

import com.example.listingservice.dto.internal.ProfileResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// This reads the URL from your application-docker.properties
@FeignClient(name = "profile-service", url = "${application.config.profile-url}")
public interface ProfileClient {

    @GetMapping("/internal/profiles/{userId}")
    ProfileResponseDTO getProfile(@PathVariable("userId") String userId);
}