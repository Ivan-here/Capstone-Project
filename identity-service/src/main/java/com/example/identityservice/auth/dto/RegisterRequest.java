package com.example.identityservice.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public record RegisterRequest(

        @Email
        @NotBlank
        @Size(max = 254)
        String email,

        @NotBlank
        @jakarta.validation.constraints.Pattern(regexp = "[A-Za-z0-9_]{3,40}")
        String username,

        @NotBlank
        @Size(min = 2, max = 60)
        String firstName,

        @NotBlank
        @Size(min = 2, max = 60)
        String lastName,

        @NotBlank
        @Size(min = 12, max = 72, message = "password must be 12 to 72 characters")
        String password,

        @Size(max = 120)
        String displayName

) {}