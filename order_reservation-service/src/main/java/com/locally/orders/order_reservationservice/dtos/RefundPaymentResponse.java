package com.locally.orders.order_reservationservice.dtos;

public record RefundPaymentResponse(
        String refundId,
        String status
) {}