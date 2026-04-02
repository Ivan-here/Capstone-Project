package com.example.listingservice.client;

import com.example.listingservice.dto.internal.FollowResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "follow-service-client", url = "${application.config.follow-url}")
public interface FollowClient {

    @GetMapping("/api/follows/{userId}/followers")
    List<FollowResponseDTO> getFollowers(@PathVariable("userId") String userId);
}
