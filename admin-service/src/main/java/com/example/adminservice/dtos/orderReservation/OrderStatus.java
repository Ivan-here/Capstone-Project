package com.example.adminservice.dtos.orderReservation;

public enum OrderStatus {
    PENDING,
    PENDING_PAYMENT,
    PAID,
    CONFIRMED,
    READY_FOR_PICKUP,
    PICKUP_CODE_VERIFIED,
    COMPLETED,
    CANCELLED,
    DISPUTED
}
