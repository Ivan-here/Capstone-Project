package com.example.stripe_paymentservice.dtos;

public record CreatePaymentIntentResponse(
        String paymentIntentId,
        String clientSecret,
        String status
) {}