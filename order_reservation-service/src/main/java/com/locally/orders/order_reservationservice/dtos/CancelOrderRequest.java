package com.locally.orders.order_reservationservice.dtos;

public record CancelOrderRequest(
        String actorUserId,
        String reason
) {}