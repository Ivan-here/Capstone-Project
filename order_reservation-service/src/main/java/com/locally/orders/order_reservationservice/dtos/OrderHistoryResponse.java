package com.locally.orders.order_reservationservice.dtos;

import com.locally.orders.order_reservationservice.model.Order;

import java.util.List;

public record OrderHistoryResponse(
        List<Order> bought,
        List<Order> sold
) {}
