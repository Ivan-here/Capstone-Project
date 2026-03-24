package com.example.stripe_paymentservice.dtos;

public record RefundPaymentRequest(
        String orderId,
        String paymentIntentId,
        Long amountCents,
        String reason
) {}