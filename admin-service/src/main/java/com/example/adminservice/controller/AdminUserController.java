package com.example.adminservice.controller;

import com.example.adminservice.dtos.user.*;
import com.example.adminservice.service.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminUserService adminUserService;

    @GetMapping
    public List<UserCredential> getAllUsers() {
        return adminUserService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserCredential getUserById(@PathVariable String id) {
        return adminUserService.getUserById(id);
    }

    @GetMapping("/search")
    public List<UserCredential> searchUsers(@RequestParam String query) {
        return adminUserService.searchUsers(query);
    }

    @GetMapping("/status/{status}")
    public List<UserCredential> getUsersByStatus(@PathVariable String status) {
        return adminUserService.getUsersByStatus(status);
    }

    @GetMapping("/role/{role}")
    public List<UserCredential> getUsersByRole(@PathVariable String role) {
        return adminUserService.getUsersByRole(role);
    }

    @PatchMapping("/{id}/status")
    public UserCredential updateStatus(
            @PathVariable String id,
            @RequestBody UpdateUserStatusRequest request
    ) {
        return adminUserService.updateStatus(id, request);
    }

    @PostMapping("/{id}/roles")
    public UpdateUserRoleResponse addRole(
            @PathVariable String id,
            @RequestBody UpdateUserRoleRequest request
    ) {
        return adminUserService.addRole(id, request);
    }

    @DeleteMapping("/{id}/roles")
    public UpdateUserRoleResponse removeRole(
            @PathVariable String id,
            @RequestBody UpdateUserRoleRequest request
    ) {
        return adminUserService.removeRole(id, request);
    }

    @PutMapping("/{id}")
    public UserCredential updateUserDetails(
            @PathVariable String id,
            @RequestBody UpdateUserDetailsRequest request
    ) {
        return adminUserService.updateUserDetails(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        adminUserService.deleteUser(id);
    }
}