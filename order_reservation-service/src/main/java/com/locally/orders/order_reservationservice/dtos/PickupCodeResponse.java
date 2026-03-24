package com.locally.orders.order_reservationservice.dtos;

public record PickupCodeResponse(
        String orderId,
        String pickupCode
) {}