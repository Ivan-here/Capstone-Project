package com.locally.orders.order_reservationservice.dtos;

public record PaymentSucceededResponse(
        String orderId,
        String status,
        String paymentStatus,
        boolean stockDeducted
) {}