package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    private String listingId;
    private String title;
    private String unit;
    private Long unitPriceCents;
    private Integer quantity;
    private Long lineTotalCents;
}
