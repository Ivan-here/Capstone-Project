package com.example.profileservice.profiles.repo;

import com.example.profileservice.profiles.model.BusinessProfile;
import com.example.profileservice.profiles.model.BusinessType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BusinessProfileRepository extends MongoRepository<BusinessProfile, String> {
    Optional<BusinessProfile> findByUserId(String userId);

    List<BusinessProfile> findByVerified(boolean verified);

    List<BusinessProfile> findByBusinessType(BusinessType businessType);
}