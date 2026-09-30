package com.example.stripe_paymentservice.dtos;
public record VerifyPaymentRequest(String orderId, String paymentIntentId, Long amountCents, String currency, String sellerUserId) {}
