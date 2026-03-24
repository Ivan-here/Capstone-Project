package com.locally.orders.order_reservationservice.dtos;

import java.util.List;

public record CreateOrderRequest(
        String shopperId,
        List<CreateOrderItemRequest> items
) {}