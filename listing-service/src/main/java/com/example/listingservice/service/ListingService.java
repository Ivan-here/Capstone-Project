package com.example.listingservice.service;

import com.example.listingservice.dto.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {

    private final ListingRepository repository;

    public Listing createListing(CreateListingDTO dto) {
        // Handle potential null tags list
        List<String> tags = dto.tags() != null ? dto.tags() : new ArrayList<>();

        Listing listing = Listing.builder()
                .ownerId(dto.ownerId())
                .type(dto.type())
                .title(dto.title())
                .description(dto.description())
                .category(dto.category())
                .tags(tags) // Store the machine vision tags!
                .price(dto.price())
                .unit(dto.unit())
                .quantity(dto.quantity())
                .expiryDate(dto.expiryDate())
                .imageUrl(dto.imageUrl())
                .status("ACTIVE") // Default status
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        log.info("Creating new listing: {} for owner: {}", dto.title(), dto.ownerId());
        return repository.save(listing);
    }

    public Listing getListingById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    public List<Listing> getAllActiveListings() {
        return repository.findByStatus("ACTIVE");
    }

    // Used by the Owner to close a listing
    public Listing closeListing(String id) {
        Listing listing = getListingById(id);
        listing.setStatus("CLOSED");
        listing.setUpdatedAt(LocalDateTime.now());
        return repository.save(listing);
    }

    // Used by Orders Service to decrease stock
    public Listing updateStock(String id, UpdateListingDTO dto) {
        Listing listing = getListingById(id);

        listing.setQuantity(dto.newQuantity());

        // Auto-close if out of stock
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