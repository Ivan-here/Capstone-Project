package com.example.identityservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @NotBlank(message = "Email or username is required")
        @jakarta.validation.constraints.Size(max = 254)
        String login,
        @NotBlank(message = "Password is required")
        @jakarta.validation.constraints.Size(max = 72)
        String password
) {}