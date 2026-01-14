package com.example.identityservice.users;

public interface UserService {
    UserCredential register(String email, String rawPassword, String displayName);
    UserCredential authenticate(String email, String rawPassword);
    UserCredential addRole(String userId, Role role);
}