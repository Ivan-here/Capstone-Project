package com.example.profileservice.profiles.dto;

import com.example.profileservice.profiles.model.PersonalProfile;
import jakarta.validation.constraints.Email;
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

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email max 120 chars")
        String email,

        String username,

        // optional fields (can be null)
        String role,
        String location,
        String about,
        String phone,
        List<String> addresses,
        List<String> preferences,
        PersonalProfile.Stats stats,
        List<PersonalProfile.FollowPerson> followingPeople,
        List<PersonalProfile.Rating> ratings
) {}
