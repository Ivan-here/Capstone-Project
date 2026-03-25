package com.locally.orders.order_reservationservice.dtos;

public record ReleaseFundsRequest(
        String orderId,
        String sellerUserId,
        Long sellerAmountCents,
        String currency
) {}