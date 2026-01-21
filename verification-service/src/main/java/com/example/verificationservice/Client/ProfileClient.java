package com.example.verificationservice.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

// "name" doesn't matter much in Docker unless using Eureka, but "url" does.
// We read the URL from application.properties so it works in Docker AND Localhost.
@FeignClient(name = "profile-service", url = "${application.config.profile-url}")
public interface ProfileClient {

    // This calls the endpoint we are about to create in Profile Service
    @PostMapping("/internal/profiles/{userId}/verify")
    void verifyProfile(@PathVariable("userId") String userId);
}