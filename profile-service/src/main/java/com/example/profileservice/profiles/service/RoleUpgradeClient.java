package com.example.profileservice.profiles.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class RoleUpgradeClient {

    private final IdentityClient identityClient;

    public void addRoleToUser(String userId, String role) {
        log.info("Requesting role upgrade via Feign: userId={}, role={}", userId, role);
        identityClient.addRole(userId, Map.of("role", role));
    }
}