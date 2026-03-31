package com.example.profileservice.profiles.dto;

import com.example.profileservice.profiles.model.BusinessType;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessProfileRequest(
        @NotNull(message = "businessType is required")
        BusinessType businessType,

        @NotBlank(message = "businessName is required")
        @Size(max = 120, message = "businessName max 120 chars")
        @JsonAlias("name")
        String businessName,

        @NotBlank(message = "address is required")
        @Size(max = 200, message = "address max 200 chars")
        String address,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email max 120 chars")
        String email,

        @Size(max = 500, message = "avatarUrl max 500 chars")
        String avatarUrl,

        // optional
        @Size(max = 1000, message = "description max 1000 chars")
        String description,
        String hours,
        String serviceArea,
        String eligibilityNotes
) {}
