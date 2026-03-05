package com.example.listingservice.service;

import com.example.listingservice.dto.*;
import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.listingservice.client.ProfileClient;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {

    private final ListingRepository repository;
    private final ProfileClient profileClient;
    private final CloudinaryService cloudinaryService; // <--- Injected Cloudinary

    // Signature updated to include the images list
    public Listing createListing(CreateListingDTO dto, List<MultipartFile> images, String expectedBusinessType) {

        // A. Call Profile Service
        log.info("Fetching profile for owner: {}", dto.ownerId());
        var profile = profileClient.getProfile(dto.ownerId());

        log.info("RAW PROFILE DATA: {}", profile);
        log.info("IS VERIFIED? {}", profile.isVerified());

        // B. CHECK VERIFICATION
        if (!profile.isVerified()) {
            throw new RuntimeException("Access Denied: You must be a VERIFIED business to post listings.");
        }

        // C. CHECK TYPE
        String actualType = profile.getBusinessType();
        if (actualType == null || !actualType.equalsIgnoreCase(expectedBusinessType)) {
            throw new RuntimeException("Access Denied: You are a " + actualType + ", but this endpoint is for " + expectedBusinessType + "s.");
        }

        // D. UPLOAD IMAGES TO CLOUDINARY
        List<String> uploadedUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile file : images) {
                try {
                    String url = cloudinaryService.uploadImage(file);
                    if (url != null) {
                        uploadedUrls.add(url);
                    }
                } catch (Exception e) {
                    log.error("Failed to upload image to Cloudinary: {}", e.getMessage());
                }
            }
        }

        // E. Create Listing
        List<String> tags = dto.tags() != null ? dto.tags() : new ArrayList<>();

        Listing listing = Listing.builder()
                .ownerId(dto.ownerId())
                .businessName(profile.getBusinessName())
                .pickupLocation(profile.getPickupLocation())
                .type(dto.type())
                .title(dto.title())
                .description(dto.description())
                .category(dto.category())
                .tags(tags)
                .price(dto.price())
                .unit(dto.unit())
                .quantity(dto.quantity())
                .expiryDate(dto.expiryDate())
                .imageUrls(uploadedUrls) // <--- Now saving the list of URLs!
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

    public Listing updateFullListing(String id, FullUpdateListingDTO dto, List<MultipartFile> images) {
        // 1. Find the existing listing
        Listing existingListing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));

        // 2. Update the text fields
        existingListing.setTitle(dto.title());
        existingListing.setPrice(BigDecimal.valueOf(dto.price()));
        existingListing.setUnit(dto.unit());
        existingListing.setDescription(dto.description());
        existingListing.setQuantity(dto.quantity()); // <-- ADDED
        existingListing.setExpiryDate(dto.expiryDate());

        // 3. Handle Images Securely: Combine retained images + new uploads
        List<String> finalImageUrls = new ArrayList<>();

        // Step 3a: Keep the old Cloudinary URLs the user didn't delete
        if (dto.retainedImages() != null && !dto.retainedImages().isEmpty()) {
            finalImageUrls.addAll(dto.retainedImages());
        }

        // Step 3b: Upload any brand new files they added
        if (images != null && !images.isEmpty()) {
            log.info("New images provided for listing {}. Uploading to Cloudinary...", id);

            List<String> newUrls = images.stream()
                    .map(cloudinaryService::uploadImage)
                    .toList();

            finalImageUrls.addAll(newUrls);
        }

        // Step 3c: Set the combined list to the listing (even if it's empty)
        existingListing.setImageUrls(finalImageUrls);

        existingListing.setUpdatedAt(LocalDateTime.now());

        // 4. Save and return
        log.info("Successfully updated listing: {}", id);
        return repository.save(existingListing);
    }
}