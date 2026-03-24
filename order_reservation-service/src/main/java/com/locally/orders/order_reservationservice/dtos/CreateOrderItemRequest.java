package com.locally.orders.order_reservationservice.dtos;

import java.util.List;

public record CreateOrderItemRequest(
        String listingId,
        Integer quantity
) {}