package com.example.listingservice.service;

import com.example.listingservice.dto.CreateListingDTO;
import com.example.listingservice.dto.internal.FollowResponseDTO;
import com.example.listingservice.dto.internal.NotificationRequest;
import com.example.listingservice.dto.FullUpdateListingDTO;
import com.example.listingservice.dto.UpdateListingDTO;
import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.example.listingservice.client.PaymentClient;
import com.example.listingservice.client.ProfileClient;
import com.example.listingservice.client.FollowClient;
import com.example.listingservice.client.NotificationClient;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ListingService {

    private static final Set<String> ADMIN_ALLOWED_STATUSES = Set.of(
            "ACTIVE",
            "OUT_OF_STOCK",
            "EXPIRED",
            "CLOSED"
    );

    private final ListingRepository repository;
    private final ProfileClient profileClient;
    private final PaymentClient paymentClient;
    private final CloudinaryService cloudinaryService;
    private final FollowClient followClient;
    private final NotificationClient notificationClient;

    public Listing createListing(CreateListingDTO dto, List<MultipartFile> images, String expectedBusinessType) {

        log.info("Fetching profile for owner: {}", dto.ownerId());
        var profile = profileClient.getProfile(dto.ownerId());

        if (!profile.isVerified()) {
            throw new RuntimeException("Access Denied: You must be a VERIFIED business to post listings.");
        }

        String actualType = profile.getBusinessType();
        if (actualType == null || !actualType.equalsIgnoreCase(expectedBusinessType)) {
            throw new RuntimeException("Access Denied: You are a " + actualType + ", but this endpoint is for " + expectedBusinessType + "s.");
        }

        if ("FARMER".equalsIgnoreCase(expectedBusinessType)) {
            var paymentProfile = paymentClient.refreshSellerStatus(dto.ownerId());
            if (paymentProfile == null || !paymentProfile.isReadyToReceivePayments()) {
                throw new RuntimeException("Farmers must complete payment onboarding before posting listings.");
            }
        }

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

        String initialVisibility = "PUBLIC";
        if ("SURPLUS_FOOD".equalsIgnoreCase(dto.type())) {
            initialVisibility = "NGO_ONLY";
        }

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
                .imageUrls(uploadedUrls)
                .status("ACTIVE")
                .visibility(initialVisibility)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Listing savedListing = repository.save(listing);
        notifyFollowersAboutNewListing(savedListing);
        return savedListing;
    }
    private void notifyFollowersAboutNewListing(Listing listing) {
        if (listing == null || listing.getId() == null || listing.getOwnerId() == null || listing.getOwnerId().isBlank()) {
            return;
        }

        try {
            List<FollowResponseDTO> followers = followClient.getFollowers(listing.getOwnerId());
            if (followers == null || followers.isEmpty()) {
                return;
            }

            String creatorName = listing.getBusinessName() != null && !listing.getBusinessName().isBlank()
                    ? listing.getBusinessName()
                    : "A creator you follow";

            String title = "New listing from someone you follow";
            String message = creatorName + " posted a new listing: " + listing.getTitle() + ".";

            for (FollowResponseDTO follower : followers) {
                String followerUserId = follower != null && follower.getUser() != null ? follower.getUser().getId() : null;
                if (followerUserId == null || followerUserId.isBlank()) {
                    continue;
                }

                try {
                    notificationClient.createNotification(new NotificationRequest(
                            followerUserId,
                            listing.getOwnerId(),
                            "FOLLOWING_NEW_LISTING",
                            title,
                            message,
                            "listing-service",
                            "LISTING",
                            listing.getId(),
                            "/product/" + listing.getId()
                    ));
                } catch (Exception ex) {
                    log.warn("Failed to create follower listing notification listingId={} recipientUserId={}", listing.getId(), followerUserId, ex);
                }
            }
        } catch (Exception ex) {
            log.warn("Failed to notify followers about listingId={}", listing.getId(), ex);
        }
    }

    public Listing getListingById(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found"));
    }

    // Safely filter active listings in Java
    public List<Listing> getAllActiveListings() {
        return repository.findAll().stream()
                .filter(listing -> !"CLOSED".equalsIgnoreCase(listing.getStatus()))
                .toList();
    }

    public List<Listing> getAllListings() {return repository.findAll();}

    public Listing closeListing(String id) {
        Listing listing = getListingById(id);
        listing.setStatus("CLOSED");
        listing.setUpdatedAt(LocalDateTime.now());
        return repository.save(listing);
    }

    public Listing updateStatus(String id, String status) {
        Listing listing = getListingById(id);
        String normalizedStatus = normalizeStatus(status);

        listing.setStatus(normalizedStatus);
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

    public Listing updateFullListing(String id, FullUpdateListingDTO dto, List<MultipartFile> images) {
        Listing existingListing = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Listing not found with id: " + id));

        existingListing.setTitle(dto.title());
        existingListing.setPrice(BigDecimal.valueOf(dto.price()));
        existingListing.setUnit(dto.unit());
        existingListing.setDescription(dto.description());

        existingListing.setQuantity(dto.quantity());
        if ("OUT_OF_STOCK".equals(existingListing.getStatus()) && dto.quantity() > 0) {
            existingListing.setStatus("ACTIVE");
        }

        existingListing.setExpiryDate(dto.expiryDate());

        List<String> finalImageUrls = new ArrayList<>();

        if (dto.retainedImages() != null && !dto.retainedImages().isEmpty()) {
            finalImageUrls.addAll(dto.retainedImages());
        }

        if (images != null && !images.isEmpty()) {
            log.info("New images provided for listing {}. Uploading to Cloudinary...", id);
            List<String> newUrls = images.stream()
                    .map(cloudinaryService::uploadImage)
                    .toList();
            finalImageUrls.addAll(newUrls);
        }

        existingListing.setImageUrls(finalImageUrls);
        existingListing.setUpdatedAt(LocalDateTime.now());

        log.info("Successfully updated listing: {}", id);
        return repository.save(existingListing);
    }

    public void deleteListingById(String id){
        log.info("Deleting listing with id: {}", id);
        repository.deleteById(id);
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase();
        if (!ADMIN_ALLOWED_STATUSES.contains(normalized)) {
            throw new RuntimeException("Unsupported listing status: " + status);
        }
        return normalized;
    }
}
