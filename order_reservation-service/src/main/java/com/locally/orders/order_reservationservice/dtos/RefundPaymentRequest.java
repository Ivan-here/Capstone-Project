package com.locally.orders.order_reservationservice.dtos;

public record RefundPaymentRequest(
        String orderId,
        String paymentIntentId,
        Long amountCents,
        String reason
) {}