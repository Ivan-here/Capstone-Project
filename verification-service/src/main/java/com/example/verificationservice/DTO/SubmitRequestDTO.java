package com.example.verificationservice.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record SubmitRequestDTO(
        @NotBlank(message = "User ID is required")
        String userId,

        @NotBlank(message = "Type is required")
        @Pattern(regexp = "FARMER|RESTAURANT|NGO", message = "Type must be FARMER, RESTAURANT, or NGO")
        String type,

        @NotBlank(message = "Document URL cannot be empty")
        String documentUrl
) {}