package com.example.identityservice.users;

public interface UserService {
    UserCredential register(String email, String rawPassword, String username, String firstName, String lastName, String displayName);
    UserCredential authenticate(String login, String rawPassword);
    UserCredential addRole(String userId, Role role);
}