package com.example.profileservice.profiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonalProfileRequest(
        @NotBlank(message = "fullName is required")
        @Size(max = 120, message = "fullName max 120 chars")
        String fullName,

        @Size(max = 30, message = "phone max 30 chars")
        String phone,

        List<@Size(max = 200, message = "address max 200 chars") String> addresses
) {}