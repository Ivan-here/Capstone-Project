package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSummaryResponse {

    private String id;
    private String ngoId;
    private String restaurantId;
    private String surplusItemId;
    private String status;
}
