package com.locally.orders.order_reservationservice.dtos;

public record ReleaseFundsResponse(
        String transferId,
        String status
) {}