package com.example.listingservice.controller;

import com.example.listingservice.dto.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.service.ListingService;
import com.example.listingservice.dto.CreateListingDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService service;

    // 1. POST /listings/farm (Role: FARMER)
    @PostMapping("/farm")
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createFarmListing(@Valid @RequestBody CreateListingDTO dto) {
        // You might want to force the type here to ensure safety
        // dto = new CreateListingDTO(..., "FARM_PRODUCT", ...);
        // For now, we trust the DTO validation
        return service.createListing(dto);
    }

    // 2. POST /listings/surplus (Role: RESTAURANT)
    @PostMapping("/surplus")
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createSurplusListing(@Valid @RequestBody CreateListingDTO dto) {
        return service.createListing(dto);
    }

    // 3. GET /listings (Browse)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Listing> getAllListings() {
        return service.getAllActiveListings();
    }

    // 4. GET /listings/{id} (View Details)
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Listing getListing(@PathVariable String id) {
        return service.getListingById(id);
    }

    // 5. PATCH /listings/{id}/close (Owner closes)
    @PatchMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    public Listing closeListing(@PathVariable String id) {
        return service.closeListing(id);
    }

    // 6. PATCH /listings/{id}/quantity (Internal use by Orders Service)
    @PatchMapping("/{id}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public Listing updateStock(@PathVariable String id, @Valid @RequestBody UpdateListingDTO dto) {
        return service.updateStock(id, dto);
    }
}