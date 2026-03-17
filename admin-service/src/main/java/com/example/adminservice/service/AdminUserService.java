package com.example.adminservice.service;

import com.example.adminservice.clients.IdentityServiceClient;
import com.example.adminservice.dtos.user.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final IdentityServiceClient identityServiceClient;

    public List<UserCredential> getAllUsers() {
        return identityServiceClient.getAllUsers();
    }

    public UserCredential getUserById(String id) {
        return identityServiceClient.getUserById(id);
    }

    public List<UserCredential> searchUsers(String query) {
        return identityServiceClient.searchUsers(query);
    }

    public List<UserCredential> getUsersByStatus(String status) {
        return identityServiceClient.getUsersByStatus(UserStatus.valueOf(status.toUpperCase()));
    }

    public List<UserCredential> getUsersByRole(String role) {
        return identityServiceClient.getUsersByRole(Role.valueOf(role.toUpperCase()));
    }

    public UserCredential updateStatus(String id, UpdateUserStatusRequest request) {
        return identityServiceClient.updateStatus(
                id,
                UserStatus.valueOf(request.getStatus().toUpperCase())
        );
    }

    public UpdateUserRoleResponse addRole(String id, UpdateUserRoleRequest request) {
        return identityServiceClient.addRoleInternal(
                id,
                new AddRoleRequest(Role.valueOf(request.getRole().toUpperCase()))
        );
    }

    public UpdateUserRoleResponse removeRole(String id, UpdateUserRoleRequest request) {
        return identityServiceClient.removeRoleInternal(
                id,
                new AddRoleRequest(Role.valueOf(request.getRole().toUpperCase()))
        );
    }

    public UserCredential updateUserDetails(String id, UpdateUserDetailsRequest request) {
        return identityServiceClient.updateUserDetails(
                id,
                request.getFirstName(),
                request.getLastName(),
                request.getDisplayName(),
                request.getEmail(),
                request.getUsername(),
                UserStatus.valueOf(request.getStatus().toUpperCase())
        );
    }

    public void deleteUser(String id) {
        identityServiceClient.deleteUser(id);
    }
}