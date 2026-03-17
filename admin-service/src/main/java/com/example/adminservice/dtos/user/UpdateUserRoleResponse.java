package com.example.adminservice.dtos.user;
import lombok.Data;
import java.util.Set;

@Data
public class UpdateUserRoleResponse {

    private String userId;
    private Set<String> roles;
}
