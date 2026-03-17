package com.example.adminservice.dtos.user;

import jakarta.validation.constraints.NotNull;

public record AddRoleRequest(
        @NotNull Role role
) {}