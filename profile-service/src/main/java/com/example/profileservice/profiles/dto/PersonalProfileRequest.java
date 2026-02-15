package com.example.profileservice.profiles.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record PersonalProfileRequest(
        @NotBlank(message = "firstName is required")
        @Size(max = 60, message = "firstName max 60 chars")
        String firstName,

        @NotBlank(message = "lastName is required")
        @Size(max = 60, message = "lastName max 60 chars")
        String lastName,

        @Size(max = 30, message = "contactNumber max 30 chars")
        String contactNumber,

        @Size(max = 120, message = "email max 120 chars")
        String email
) {}