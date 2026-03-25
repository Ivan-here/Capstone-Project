package com.locally.orders.order_reservationservice.model;

public enum PaymentStatus {
    REQUIRES_PAYMENT,
    PAYMENT_PROCESSING,
    HELD,
    RELEASE_PENDING,
    RELEASED,
    FAILED,
    CANCELLED,
    REFUNDED
}