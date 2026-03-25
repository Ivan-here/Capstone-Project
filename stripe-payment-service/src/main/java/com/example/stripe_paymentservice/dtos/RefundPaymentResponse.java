package com.example.stripe_paymentservice.dtos;

public record RefundPaymentResponse(
        String refundId,
        String status
) {}