package com.example.stripe_paymentservice.dtos;

public record PaymentSucceededRequest(
        String paymentIntentId
) {}