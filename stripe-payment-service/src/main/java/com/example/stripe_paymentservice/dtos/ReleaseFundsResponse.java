package com.example.stripe_paymentservice.dtos;

public record ReleaseFundsResponse(
        String transferId,
        String status
) {}