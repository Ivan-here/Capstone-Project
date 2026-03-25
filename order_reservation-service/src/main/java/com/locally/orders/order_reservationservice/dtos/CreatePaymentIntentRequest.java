package com.locally.orders.order_reservationservice.dtos;

public record CreatePaymentIntentRequest(
        String orderId,
        String sellerUserId,
        Long grossAmountCents,
        Long platformFeeCents,
        Long sellerAmountCents,
        String currency
) {}