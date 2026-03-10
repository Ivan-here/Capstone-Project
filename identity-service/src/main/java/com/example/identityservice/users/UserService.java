package com.example.identityservice.users;

import java.util.List;

public interface UserService {
    UserCredential register(String email, String rawPassword, String username, String firstName, String lastName, String displayName);
    UserCredential authenticate(String login, String rawPassword);
    UserCredential addRole(String userId, Role role);
    List<UserCredential> getAllUsers();
    UserCredential removeRole(String userId, Role role);
    UserCredential updateStatus(String userId, UserStatus status);
    UserCredential updateUserDetails(String userId, String firstName, String lastName, String displayName, String email, String username, UserStatus status);
    List<UserCredential> getUsersByStatus(UserStatus status);
    List<UserCredential> getUsersByRole(Role role);
    List<UserCredential> searchUsers(String query);
    void deleteUser(String userId);
    UserCredential getUserById(String userId);
}