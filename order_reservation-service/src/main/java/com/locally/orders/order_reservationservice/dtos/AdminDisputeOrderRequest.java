package com.locally.orders.order_reservationservice.dtos;

public record AdminDisputeOrderRequest(
        String adminUserId,
        String reason,
        boolean refundPayment
) {}
