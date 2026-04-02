package com.example.adminservice.service;

import com.example.adminservice.clients.IdentityServiceClient;
import com.example.adminservice.clients.ListingServiceClient;
import com.example.adminservice.clients.OrderReservationServiceClient;
import com.example.adminservice.clients.ProfileServiceClient;
import com.example.adminservice.clients.VerificationServiceClient;
import com.example.adminservice.dtos.dashboard.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminDashboardService {

    private final IdentityServiceClient identityServiceClient;
    private final ListingServiceClient listingServiceClient;
    private final VerificationServiceClient verificationServiceClient;
    private final OrderReservationServiceClient orderReservationServiceClient;
    private final ProfileServiceClient profileServiceClient;

    public DashboardStatsResponse getStats() {
        long totalUsers = safeCount("users", () -> identityServiceClient.getAllUsers().size());
        long totalListings = safeCount("listings", () -> listingServiceClient.getAllAdminListings().size());
        long pendingVerifications = safeCount("pending verifications", () -> verificationServiceClient.getQueue().size());
        long totalOrders = safeCount("orders", () -> orderReservationServiceClient.getAllOrders().size());
        long totalReservations = safeCount("reservations", () -> orderReservationServiceClient.getAllReservations().size());
        long totalBusinessProfiles = safeCount("business profiles", () -> profileServiceClient.getAllBusinessProfiles().size());

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalListings(totalListings)
                .pendingVerifications(pendingVerifications)
                .totalOrders(totalOrders)
                .totalReservations(totalReservations)
                .totalBusinessProfiles(totalBusinessProfiles)
                .build();
    }

    private long safeCount(String label, CountSupplier supplier) {
        try {
            return supplier.get();
        } catch (Exception ex) {
            log.warn("Failed to load admin dashboard {} count: {}", label, ex.getMessage());
            return 0L;
        }
    }

    @FunctionalInterface
    private interface CountSupplier {
        long get();
    }
}
