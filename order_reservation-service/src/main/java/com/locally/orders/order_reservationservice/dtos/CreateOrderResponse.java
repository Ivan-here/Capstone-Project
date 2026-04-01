package com.locally.orders.order_reservationservice.dtos;

public record CreateOrderResponse(
        String orderId,
        String status,
        String paymentStatus,
        Long grossAmountCents,
        String currency,
        Boolean requiresPayment
) {}
