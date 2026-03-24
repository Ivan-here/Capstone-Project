package com.locally.orders.order_reservationservice.dtos;

public record VerifyPickupCodeRequest(
        String code,
        String sellerUserId
) {}