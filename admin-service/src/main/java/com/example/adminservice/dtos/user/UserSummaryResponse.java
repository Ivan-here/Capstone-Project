package com.example.adminservice.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSummaryResponse {
    private String id;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String displayName;
    private Set<String> roles;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
}