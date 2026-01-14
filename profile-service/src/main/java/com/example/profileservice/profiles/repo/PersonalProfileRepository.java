package com.example.profileservice.profiles.repo;

import com.example.profileservice.profiles.model.PersonalProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PersonalProfileRepository extends MongoRepository<PersonalProfile, String> {
    Optional<PersonalProfile> findByUserId(String userId);
}