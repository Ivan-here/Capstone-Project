package com.example.profileservice.profiles.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@FeignClient(
        name = "identityClient",
        url = "${clients.identityBaseUrl}",
        configuration = IdentityFeignConfig.class
)
public interface IdentityClient {

    @DeleteMapping("/internal/users/{userId}/roles:remove")
    Map<String, Object> removeRole(@PathVariable("userId") String userId, @RequestBody Map<String, Object> body);

    @PostMapping("/internal/users/{userId}/roles:add")
    Map<String, Object> addRole(
            @PathVariable("userId") String userId,
            @RequestBody Map<String, Object> body
    );

    @DeleteMapping("/users/{userId}")
    void deleteUser(@PathVariable("userId") String userId);
}
