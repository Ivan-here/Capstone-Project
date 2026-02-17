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
@CrossOrigin(origins = "http://localhost:5173")
public class ListingController {

    private final ListingService service;

    // 1. Endpoint for FARMERS only
    @PostMapping("/farm")
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createFarmListing(@Valid @RequestBody CreateListingDTO dto) {
        // We pass "FARMER" as the expected type
        return service.createListing(dto, "FARMER");
    }

    // 2. Endpoint for RESTAURANTS only
    @PostMapping("/surplus")
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createSurplusListing(@Valid @RequestBody CreateListingDTO dto) {
        // We pass "RESTAURANT" as the expected type
        return service.createListing(dto, "RESTAURANT");
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