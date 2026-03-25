package com.example.stripe_paymentservice.dtos;

public record ReleaseFundsRequest(
        String orderId,
        String sellerUserId,
        Long sellerAmountCents,
        String currency
) {}