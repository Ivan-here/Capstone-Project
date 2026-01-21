package com.locally.orders.order_reservationservice.service;


import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.StatusHistory;
import com.locally.orders.order_reservationservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public void placeOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING.toString());

        List<StatusHistory> history = new ArrayList<>();

        history.add(new StatusHistory(OrderStatus.PENDING, LocalDateTime.now(), "System"));
        order.setHistory(history);

        orderRepository.save(order);
    }

    public void updateOrder(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(()-> new RuntimeException("Order not found"));
        order.setStatus(newStatus.name());

        List<StatusHistory> history = order.getHistory();
        if (history == null) {
            history = new ArrayList<>();
        }

        history.add(new StatusHistory(newStatus, LocalDateTime.now(), "Restaurant_Admin"));
        order.setHistory(history);

        orderRepository.save(order);
    }


}

