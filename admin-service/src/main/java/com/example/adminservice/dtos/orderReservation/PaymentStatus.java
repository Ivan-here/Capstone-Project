package com.example.adminservice.dtos.orderReservation;

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
