package com.example.adminservice.clients;

import com.example.identityservice.users.UserStatus;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.example.identityservice.users.UserCredential;
import com.example.identityservice.users.Role;
import com.example.identityservice.internal.dto.AddRoleRequest;

import java.util.List;
import java.util.Map;

@FeignClient(
        name = "identityClient",
        url = "${client.identityBaseUrl}"
)
public interface IdentityServiceClient {
    @GetMapping("/users")
    List<UserCredential> getAllUsers();

    @GetMapping("/users/{id}")
    UserCredential getUserById(@PathVariable("id") String id);

    @GetMapping("/users/search")
    List<UserCredential> searchUsers(@RequestParam("query") String query);

    @GetMapping("/users/status/{status}")
    List<UserCredential> getUsersByStatus(@PathVariable("status") UserStatus status);

    @GetMapping("/users/role/{role}")
    List<UserCredential> getUsersByRole(@PathVariable("role") Role role);

    @PatchMapping("/users/{id}/status")
    UserCredential updateStatus(
            @PathVariable("id") String id,
            @RequestParam("status") UserStatus status
    );

    @PutMapping("/users/{id}")
    UserCredential updateUserDetails(
            @PathVariable("id") String id,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "displayName", required = false) String displayName,
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("status") UserStatus status
    );

    @PostMapping("/users/{id}/roles")
    UserCredential addRole(
            @PathVariable("id") String id,
            @RequestParam("role") Role role
    );

    @DeleteMapping("/users/{id}/roles")
    UserCredential removeRole(
            @PathVariable("id") String id,
            @RequestParam("role") Role role
    );

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable("id") String id);

    // ---------------- INTERNAL ----------------

    @PostMapping("/internal/users/{userId}/roles:add")
    Map<String, Object> addRoleInternal(
            @PathVariable("userId") String userId,
            @RequestHeader("X-Internal-Secret") String secret,
            @RequestBody AddRoleRequest request
    );

    @DeleteMapping("/internal/users/{userId}/roles:remove")
    Map<String, Object> removeRoleInternal(
            @PathVariable("userId") String userId,
            @RequestHeader("X-Internal-Secret") String secret,
            @RequestBody AddRoleRequest request
    );
}
