package com.locally.orders.order_reservationservice.model;

public enum OrderStatus {
    PENDING_PAYMENT,
    PAID,
    CONFIRMED,
    READY_FOR_PICKUP,
    PICKUP_CODE_VERIFIED,
    COMPLETED,
    CANCELLED,
    DISPUTED
}