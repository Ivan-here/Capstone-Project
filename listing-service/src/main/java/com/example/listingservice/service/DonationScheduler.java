package com.example.listingservice.service;

import com.example.listingservice.model.Listing;
import com.example.listingservice.repository.ListingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DonationScheduler {

    private final ListingRepository repository;

    @Scheduled(fixedRate = 3600000) // Runs every hour
    public void updateDonationVisibility() {
        List<Listing> exclusiveListings = repository.findByVisibility("NGO_ONLY");
        LocalDateTime now = LocalDateTime.now();

        for (Listing listing : exclusiveListings) {
            if (listing.getExpiryDate() == null) continue;

            // Rule 1: Older than 5 days
            boolean fiveDaysPassed = listing.getCreatedAt().plusDays(5).isBefore(now);

            // Rule 2: Half-life logic (Time passed > 50% of shelf life)
            long totalShelfLife = Duration.between(listing.getCreatedAt(), listing.getExpiryDate()).toSeconds();
            long timeElapsed = Duration.between(listing.getCreatedAt(), now).toSeconds();
            boolean halfLifeReached = timeElapsed > (totalShelfLife / 2);

            if (fiveDaysPassed || halfLifeReached) {
                listing.setVisibility("PUBLIC");
                listing.setUpdatedAt(now);
                repository.save(listing);
                log.info("Donation listing {} is now PUBLIC for all users.", listing.getId());
            }
        }
    }
}