package com.example.listingservice.repository;

import com.example.listingservice.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface ListingRepository extends MongoRepository<Listing, String> {

    List<Listing> findByOwnerId(String ownerId);

    List<Listing> findByType(String type);

    // RESTORED: This is required by DonationScheduler.java to find items that haven't crossed their half-life
    @Query("{ '$or': [ { 'visibility': ?0 }, { 'visibility': { $exists: false } }, { 'visibility': null } ] }")
    List<Listing> findByVisibility(String visibility);
}