package com.example.profileservice.profiles.repo;

import com.example.profileservice.profiles.model.BusinessProfile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BusinessProfileRepository extends MongoRepository<BusinessProfile, String> {
    Optional<BusinessProfile> findByUserId(String userId);
}