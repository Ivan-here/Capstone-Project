package com.locally.orders.order_reservationservice.dtos;

public record CreatePaymentIntentResponse(
        String paymentIntentId,
        String clientSecret,
        String status
) {}