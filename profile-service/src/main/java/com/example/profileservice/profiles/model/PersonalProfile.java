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

    // UI fields
    private String firstName;
    private String lastName;
    private String location;   // "Toronto, ON"
    private String role;
    private String about;
    private String username;
    private String displayName;

    private String phone;
    private String email;

    private List<String> addresses = new ArrayList<>();
    private List<String> preferences = new ArrayList<>();

    private Stats stats = new Stats();
    private List<FollowPerson> followingPeople = new ArrayList<>();
    private List<Rating> ratings = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    @Data
    public static class Stats {
        private int reviews = 0;
        private int purchases = 0;
        private int following = 0;
        private double avgRating = 0.0;
        private int followers = 0;
    }

    @Data
    public static class FollowPerson {
        private String id;
        private String name;
    }

    @Data
    public static class Rating {
        private String id;
        private String itemName;
        private int rating;
        private String reviewedAt;
        private String text;
    }
}