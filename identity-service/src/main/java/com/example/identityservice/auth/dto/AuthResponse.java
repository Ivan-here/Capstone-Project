package com.example.identityservice.auth.dto;

import com.example.identityservice.users.Role;
import com.example.identityservice.users.UserStatus;

import java.util.Set;

public record AuthResponse(

        String userId,

        String email,
        String username,

        String firstName,
        String lastName,
        String displayName,

        Set<Role> roles,
        UserStatus status,

        String accessToken,
        long expiresInSeconds

) {}