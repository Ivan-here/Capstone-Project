package com.example.identityservice.users;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Document("users")
public class UserCredential {

    @Id
    private String id;

    @Indexed(unique = true)
    private String email;

    private String passwordHash;
    private String username;
    private String displayName;

    private String firstName;
    private String lastName;

    private Set<Role> roles = new HashSet<>();

    private UserStatus status = UserStatus.ACTIVE;

    private Instant createdAt;
    private Instant updatedAt;
}