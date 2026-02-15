package com.example.identityservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegisterRequest(

        @Email
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 4, max = 50, message = "username must be between 4 and 50 characters")
        String username,

        @NotBlank
        @Size(min = 2, max = 60)
        String firstName,

        @NotBlank
        @Size(min = 2, max = 60)
        String lastName,

        @NotBlank
        @Size(min = 8, message = "password must be at least 8 characters")
        String password,

        @Size(max = 120)
        String displayName

) {}