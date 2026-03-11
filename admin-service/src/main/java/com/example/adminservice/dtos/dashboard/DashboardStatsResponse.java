package com.example.adminservice.dtos.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private long totalUsers;
    private long totalListings;
    private long pendingVerifications;
    private long totalOrders;
    private long totalReservations;
    private long totalBusinessProfiles;
}