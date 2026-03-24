package com.example.stripe_paymentservice.dtos;

public record PaymentSucceededResponse(
        String orderId,
        String status,
        String paymentStatus,
        boolean stockDeducted
) {}