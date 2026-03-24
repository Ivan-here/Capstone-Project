package com.locally.orders.order_reservationservice.dtos;

public record OrderPaymentIntentResponse(
        String orderId,
        String paymentIntentId,
        String clientSecret,
        String paymentStatus
) {}