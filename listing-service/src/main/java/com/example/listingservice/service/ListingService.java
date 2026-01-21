package com.example.listingservice.service;

import com.example.listingservice.dto.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.listingservice.client.ProfileClient; // Fixed import

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {

    private final ListingRepository repository;
    private final ProfileClient profileClient;

    public Listing createListing(CreateListingDTO dto, String expectedBusinessType) {

        // A. Call Profile Service
        log.info("Fetching profile for owner: {}", dto.ownerId());
        var profile = profileClient.getProfile(dto.ownerId());

        // --- DEBUG LOGGING START ---
        log.info("RAW PROFILE DATA: {}", profile);
        log.info("IS VERIFIED? {}", profile.isVerified());

        // B. CHECK VERIFICATION (Using Helper Method)
        if (!profile.isVerified()) {
            throw new RuntimeException("Access Denied: You must be a VERIFIED business to post listings.");
        }

        // C. CHECK TYPE (Using Helper Method)
        String actualType = profile.getBusinessType();
        if (actualType == null || !actualType.equalsIgnoreCase(expectedBusinessType)) {
            throw new RuntimeException("Access Denied: You are a " + actualType + ", but this endpoint is for " + expectedBusinessType + "s.");
        }

        // D. Create Listing (Auto-filling data using Helper Methods)
        List<String> tags = dto.tags() != null ? dto.tags() : new ArrayList<>();

        Listing listing = Listing.builder()
                .ownerId(dto.ownerId())
                .businessName(profile.getBusinessName())   // <--- Helper
                .pickupLocation(profile.getPickupLocation()) // <--- Helper
                .type(dto.type())
                .title(dto.title())
                .description(dto.description())
                .category(dto.category())
                .tags(tags)
                .price(dto.price())
                .unit(dto.unit())
                .quantity(dto.quantity())
                .expiryDate(dto.expiryDate())
                .imageUrl(dto.imageUrl())
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return repository.save(listing);
    }

    public Listing getListingById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    public List<Listing> getAllActiveListings() {
        return repository.findByStatus("ACTIVE");
    }

    public Listing closeListing(String id) {
        Listing listing = getListingById(id);
        listing.setStatus("CLOSED");
        listing.setUpdatedAt(LocalDateTime.now());
        return repository.save(listing);
    }

    public Listing updateStock(String id, UpdateListingDTO dto) {
        Listing listing = getListingById(id);

        listing.setQuantity(dto.newQuantity());

        if (dto.newQuantity() == 0) {
            listing.setStatus("OUT_OF_STOCK");
        }

        listing.setUpdatedAt(LocalDateTime.now());
        return repository.save(listing);
    }

    public List<Listing> getAllListings() {
        return repository.findByStatus("ACTIVE");
    }
}