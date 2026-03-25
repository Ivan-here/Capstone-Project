package com.example.listingservice.controller;

import com.example.listingservice.dto.CreateListingDTO;
import com.example.listingservice.dto.FullUpdateListingDTO;
import com.example.listingservice.dto.UpdateListingDTO;
import com.example.listingservice.model.Listing;
import com.example.listingservice.service.ListingService;
import com.example.listingservice.repository.ListingRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/listings")
@RequiredArgsConstructor
public class ListingController {

    private final ListingService service;
    private final ListingRepository repository;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @PostMapping(value = "/farm", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createFarmListing(
            @RequestPart("listing") @Valid CreateListingDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return service.createListing(dto, images, "FARMER");
    }

    @PostMapping(value = "/surplus", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Listing createSurplusListing(
            @RequestPart("listing") @Valid CreateListingDTO dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        return service.createListing(dto, images, "RESTAURANT");
    }

    // --- THE BULLETPROOF FIX ---
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Listing> getAllListings(
            @RequestParam(required = false, defaultValue = "SHOPPER") String role,
            @RequestParam(required = false) String userId
    ) {
        // 1. Fetch absolutely everything to bypass MongoDB query bugs
        List<Listing> allListings = repository.findAll();

        // 2. Filter out anything that is explicitly "CLOSED"
        List<Listing> activeListings = allListings.stream()
                .filter(listing -> !"CLOSED".equalsIgnoreCase(listing.getStatus()))
                .toList();

        // 3. OWNER HUB: See all your personal items (both PUBLIC and NGO_ONLY)
        if (userId != null && !userId.isEmpty()) {
            return activeListings.stream()
                    .filter(listing -> userId.equals(listing.getOwnerId()))
                    .toList();
        }
// --- UPDATED NGO CHECK: Be more flexible with the role string ---
        boolean isNgo = role != null && (role.equalsIgnoreCase("NGO") || role.toUpperCase().contains("NGO"));

        if (isNgo) {
            // Return EVERYTHING that is active (Public + NGO_ONLY)
            return activeListings;
        }
        // 5. PUBLIC BROWSE (Shoppers): Keep only PUBLIC items (and old items with null visibility)
        return activeListings.stream()
                .filter(listing -> {
                    String vis = listing.getVisibility();
                    // If visibility is missing (old data) OR it equals PUBLIC, show it!
                    return vis == null || vis.equalsIgnoreCase("PUBLIC");
                })
                .toList();
    }

    @GetMapping("/admin")
    @ResponseStatus(HttpStatus.OK)
    public List<Listing> getAllAdminListings() {
        return service.getAllListings();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Listing getListing(@PathVariable String id) {
        return service.getListingById(id);
    }

    @PatchMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    public Listing closeListing(@PathVariable String id) {
        return service.closeListing(id);
    }

    @PatchMapping("/{id}/quantity")
    @ResponseStatus(HttpStatus.OK)
    public Listing updateStock(@PathVariable String id, @Valid @RequestBody UpdateListingDTO dto) {
        return service.updateStock(id, dto);
    }

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