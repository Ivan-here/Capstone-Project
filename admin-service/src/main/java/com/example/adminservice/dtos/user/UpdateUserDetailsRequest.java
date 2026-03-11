package com.example.adminservice.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserDetailsRequest {
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;
    private String username;
    private String status;
}