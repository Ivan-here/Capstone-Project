package com.locally.orders.order_reservationservice.dtos;

public record PaymentSucceededRequest(
        String paymentIntentId
) {}