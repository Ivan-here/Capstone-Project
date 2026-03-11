package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryResponse {
    private String id;
    private String shopperId;
    private String restaurantId;
    private List<String> items;
    private String status;
}