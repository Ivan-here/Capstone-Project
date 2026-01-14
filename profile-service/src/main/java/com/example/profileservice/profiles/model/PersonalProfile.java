package com.example.profileservice.profiles.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Document("personal_profiles")
public class PersonalProfile {

    @Id
    private String id;

    @Indexed(unique = true)
    private String userId;

    private String fullName;
    private String phone;
    private String email;

    private List<String> addresses = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}