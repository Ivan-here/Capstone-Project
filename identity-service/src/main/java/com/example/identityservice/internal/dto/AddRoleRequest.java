package com.example.identityservice.internal.dto;

import com.example.identityservice.users.Role;
import jakarta.validation.constraints.NotNull;

public record AddRoleRequest(
        @NotNull Role role
) {}