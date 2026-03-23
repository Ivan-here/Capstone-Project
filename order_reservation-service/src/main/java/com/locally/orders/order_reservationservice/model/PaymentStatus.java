package com.locally.orders.order_reservationservice.model;

public enum PaymentStatus {
    REQUIRES_PAYMENT,
    PROCESSING,
    PAID,
    FAILED,
    CANCELLED,
    REFUNDED
}