package com.example.listingservice.controller;

import com.locally.security.Access;

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
    private final com.example.listingservice.service.InventoryService inventory;

    public record StockAdjustment(String operationId, int delta) {}

    @PostMapping("/internal/{id}/stock")
    public Listing adjustStock(@PathVariable String id, @RequestBody StockAdjustment adjustment) {
        return inventory.adjust(id, adjustment.operationId(), adjustment.delta());
    }
    private final ListingRepository repository;

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        if (ex instanceof org.springframework.web.server.ResponseStatusException status) return ResponseEntity.status(status.getStatusCode()).body("Request denied");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid listing request");
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
        List<Listing> allListings = repository.findAll().stream().filter(this::visible).toList();

        // 2. Filter out anything that is explicitly "CLOSED"
        List<Listing> activeListings = allListings.stream()
                .filter(listing -> !"CLOSED".equalsIgnoreCase(listing.getStatus()))
                .toList();

        // 3. OWNER HUB: See all your personal items (both PUBLIC and NGO_ONLY)
        // (Owners need to see expired/out-of-stock items so they can manage/delete them)
        if (userId != null && !userId.isEmpty()) {
            return activeListings.stream()
                    .filter(listing -> userId.equals(listing.getOwnerId()))
                    .toList();
        }

        // --- NEW: THE PURCHASABLE FILTER ---
        // Ensure items have stock and have not passed their expiry date
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        List<Listing> purchasableListings = activeListings.stream()
                .filter(listing -> listing.getQuantity() != null && listing.getQuantity() > 0)
                .filter(listing -> !"OUT_OF_STOCK".equalsIgnoreCase(listing.getStatus()))
                .filter(listing -> listing.getExpiryDate() == null || listing.getExpiryDate().isAfter(now))
                .toList();

        // 4. UPDATED NGO CHECK: Be more flexible with the role string ---
        boolean isNgo = Access.hasRole("NGO") || Access.privileged();

        if (isNgo) {
            // Return EVERYTHING that is purchasable (Public + NGO_ONLY)
            return purchasableListings;
        }

        // 5. PUBLIC BROWSE (Shoppers): Keep only PUBLIC items that are purchasable
        return purchasableListings.stream()
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
        Listing listing = service.getListingById(id);
        if (!visible(listing)) Access.denied();
        return listing;
    }

    @PatchMapping("/{id}/close")
    @ResponseStatus(HttpStatus.OK)
    public Listing closeListing(@PathVariable String id) {
        return service.closeListing(id);
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public Listing updateStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return service.updateStatus(id, status);
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

    @DeleteMapping("/internal/owner/{ownerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteListingsByOwnerId(@PathVariable String ownerId) {
        service.deleteListingsByOwnerId(ownerId);
    }
    private boolean visible(Listing listing) {
        return Access.privileged() || Access.owns(listing.getOwnerId()) || Access.hasRole("NGO")
            || listing.getVisibility() == null || "PUBLIC".equals(listing.getVisibility());
    }
}
