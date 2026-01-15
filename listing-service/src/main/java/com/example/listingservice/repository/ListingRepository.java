package com.example.listingservice.repository;

import com.example.listingservice.model.Listing;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ListingRepository extends MongoRepository<Listing, String> {

    // Find all products posted by a specific farmer/restaurant
    List<Listing> findByOwnerId(String ownerId);

    // Find all products of a specific type (e.g., just SURPLUS_FOOD)
    List<Listing> findByType(String type);

    // Find all active listings (ignoring expired ones)
    List<Listing> findByStatus(String status);
}