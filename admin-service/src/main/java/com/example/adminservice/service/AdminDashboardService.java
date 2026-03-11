package com.example.adminservice.service;

import com.example.adminservice.clients.IdentityServiceClient;
import com.example.adminservice.clients.ListingServiceClient;
import com.example.adminservice.clients.OrderReservationServiceClient;
import com.example.adminservice.clients.ProfileServiceClient;
import com.example.adminservice.clients.VerificationServiceClient;
import com.example.adminservice.dtos.dashboard.DashboardStatsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final IdentityServiceClient identityServiceClient;
    private final ListingServiceClient listingServiceClient;
    private final VerificationServiceClient verificationServiceClient;
    private final OrderReservationServiceClient orderReservationServiceClient;
    private final ProfileServiceClient profileServiceClient;

    public DashboardStatsResponse getStats() {
        long totalUsers = identityServiceClient.getAllUsers().size();
        long totalListings = listingServiceClient.getAllAdminListings().size();
        long pendingVerifications = verificationServiceClient.getQueue().size();
        long totalOrders = orderReservationServiceClient.getAllOrders().size();
        long totalReservations = orderReservationServiceClient.getAllReservations().size();
        long totalBusinessProfiles = profileServiceClient.getAllBusinessProfiles().size();

        return DashboardStatsResponse.builder()
                .totalUsers(totalUsers)
                .totalListings(totalListings)
                .pendingVerifications(pendingVerifications)
                .totalOrders(totalOrders)
                .totalReservations(totalReservations)
                .totalBusinessProfiles(totalBusinessProfiles)
                .build();
    }
}