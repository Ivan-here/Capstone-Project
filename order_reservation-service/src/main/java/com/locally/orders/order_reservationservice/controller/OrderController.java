package com.locally.orders.order_reservationservice.controller;


import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody Order order) {
        orderService.placeOrder(order);
        return "Order placed successfully. Your order id is " + order.getId();
    }

    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public String updateOrderStatus(@PathVariable String id, @RequestParam OrderStatus newStatus ) {
        orderService.updateOrder(id,newStatus);
        return "Order status updated to " + newStatus;
    }
}
