package com.example.profileservice.profiles.dto;

import com.example.profileservice.profiles.model.BusinessType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record BusinessProfileRequest(
        @NotNull(message = "businessType is required")
        BusinessType businessType,

        @NotBlank(message = "businessName is required")
        @Size(max = 160, message = "businessName max 160 chars")
        String businessName,

        @NotBlank(message = "address is required")
        @Size(max = 200, message = "address max 200 chars")
        String address,

        // Optional fields, depending on a businessType enforced in service
        @Size(max = 80, message = "hours max 80 chars")
        String hours,

        @Size(max = 300, message = "pickupInstructions max 300 chars")
        String pickupInstructions,

        @Size(max = 200, message = "serviceArea max 200 chars")
        String serviceArea,

        @Size(max = 250, message = "eligibilityNotes max 250 chars")
        String eligibilityNotes
) {}