package com.example.stripe_paymentservice.dtos;

public record CreatePaymentIntentRequest(
        String orderId,
        String sellerUserId,
        Long grossAmountCents,
        Long platformFeeCents,
        Long sellerAmountCents,
        String currency
) {}