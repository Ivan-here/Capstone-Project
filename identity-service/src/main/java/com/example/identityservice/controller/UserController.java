package com.example.identityservice.controller;

import com.example.identityservice.users.Role;
import com.example.identityservice.users.UserCredential;
import com.example.identityservice.users.UserService;
import com.example.identityservice.users.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @GetMapping
    public List<UserCredential> getAllUsers() {
        log.info("GET /users - fetching all users");
        return userService.getAllUsers();
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    public UserCredential getUserById(@PathVariable String id) {
        log.info("GET /users/{} - fetching user", id);
        return userService.getUserById(id);
    }

    // SEARCH USERS
    @GetMapping("/search")
    public List<UserCredential> searchUsers(@RequestParam String query) {
        log.info("GET /users/search - query={}", query);
        return userService.searchUsers(query);
    }

    // FILTER USERS BY STATUS
    @GetMapping("/status/{status}")
    public List<UserCredential> getUsersByStatus(@PathVariable UserStatus status) {
        log.info("GET /users/status/{} - filtering users", status);
        return userService.getUsersByStatus(status);
    }

    // FILTER USERS BY ROLE
    @GetMapping("/role/{role}")
    public List<UserCredential> getUsersByRole(@PathVariable Role role) {
        log.info("GET /users/role/{} - filtering users", role);
        return userService.getUsersByRole(role);
    }

    // UPDATE USER STATUS
    @PatchMapping("/{id}/status")
    public UserCredential updateStatus(
            @PathVariable String id,
            @RequestParam UserStatus status
    ) {
        log.info("PATCH /users/{}/status - new status={}", id, status);
        return userService.updateStatus(id, status);
    }

    // UPDATE USER DETAILS
    @PutMapping("/{id}")
    public UserCredential updateUserDetails(
            @PathVariable String id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam(required = false) String displayName,
            @RequestParam String email,
            @RequestParam String username,
            @RequestParam UserStatus status
    ) {
        log.info("PUT /users/{} - updating details", id);

        return userService.updateUserDetails(
                id,
                firstName,
                lastName,
                displayName,
                email,
                username,
                status
        );
    }
    // DELETE USER
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable String id) {
        log.info("DELETE /users/{} - deleting user", id);
        userService.deleteUser(id);
    }
}