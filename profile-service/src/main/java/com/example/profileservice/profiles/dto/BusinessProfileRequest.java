package com.example.profileservice.profiles.dto;

import com.example.profileservice.profiles.model.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessProfileRequest(
        @NotNull(message = "businessType is required")
        BusinessType businessType,

        @NotBlank(message = "name is required")
        @Size(max = 160, message = "name max 160 chars")
        String name,

        @NotBlank(message = "description is required")
        @Size(max = 500, message = "description max 500 chars")
        String description,

        @NotBlank(message = "address is required")
        @Size(max = 200, message = "address max 200 chars")
        String address,

        @NotBlank(message = "email is required")
        @Size(max = 120, message = "email max 120 chars")
        String email
) {}