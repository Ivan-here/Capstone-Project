package com.example.listingservice.controller;

import com.example.listingservice.dto.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.service.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity; // <-- IMPORT ADDED
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService service;

    // --- THE FIX: Intercepts RuntimeExceptions to send a clean text message instead of secure JSON ---
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // 1. Endpoint for FARMERS only (Accepts Files now)
    @PostMapping(value = "/farm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createFarmListing(
            @RequestPart("listing") @Valid CreateListingDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return service.createListing(dto, images, "FARMER");
    }

    // 2. Endpoint for RESTAURANTS only (Accepts Files now)
    @PostMapping(value = "/surplus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createSurplusListing(
            @RequestPart("listing") @Valid CreateListingDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return service.createListing(dto, images, "RESTAURANT");
    }

    // 3. GET /listings (Browse)
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Listing> getAllListings() {
        return service.getAllActiveListings();
    }

    // GET ALL FOR ADMIN PANEL
    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public List<Listing> getAllAdminListings() {
        return service.getAllListings();
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

    // 7. PUT /listings/{id} (Update full listing details and images from Farmer Hub)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Listing updateFullListing(
            @PathVariable String id,
            @RequestPart("listing") @Valid FullUpdateListingDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {

        return service.updateFullListing(id, dto, images);
    }

    @DeleteMapping("/{id}")
    public void deleteListingById(@PathVariable String id){
        service.deleteListingById(id);
    }
}