package com.locally.orders.order_reservationservice.controller;

import com.locally.orders.order_reservationservice.dtos.*;
import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.locally.orders.order_reservationservice.dtos.CancelOrderRequest;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderReservationController {

    private final OrderReservationService orderService;

    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public CreateOrderResponse createOrder(@RequestBody CreateOrderRequest request) {
        return orderService.createOrder(request);
    }

    @PostMapping("/orders/{orderId}/payment-intent")
    public OrderPaymentIntentResponse createPaymentIntentForOrder(
            @PathVariable String orderId,
            @RequestParam String shopperId
    ) {
        return orderService.createPaymentIntentForOrder(orderId, shopperId);
    }

    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Order getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("/orders")
    public List<Order> getOrdersByShopper(@RequestParam String shopperId) {
        return orderService.getOrdersByShopper(shopperId);
    }

    @GetMapping("/orders/seller/{sellerUserId}")
    public List<Order> getOrdersBySeller(@PathVariable String sellerUserId) {
        return orderService.getOrdersBySeller(sellerUserId);
    }

    @GetMapping("/orders/history")
    public OrderHistoryResponse getOrderHistory(@RequestParam String userId) {
        return orderService.getOrderHistory(userId);
    }

    @PostMapping("/internal/orders/{orderId}/payment-succeeded")
    public PaymentSucceededResponse markPaymentSucceeded(
            @PathVariable String orderId,
            @RequestBody PaymentSucceededRequest request
    ) {
        return orderService.markPaymentSucceeded(orderId, request);
    }

    @PostMapping("/orders/{orderId}/ready-for-pickup")
    public Order markReadyForPickup(
            @PathVariable String orderId,
            @RequestBody ReadyForPickupRequest request
    ) {
        return orderService.markReadyForPickup(orderId, request);
    }

    @PostMapping("/orders/{orderId}/verify-pickup-code")
    public Order verifyPickupCode(
            @PathVariable String orderId,
            @RequestBody VerifyPickupCodeRequest request
    ) {
        return orderService.verifyPickupCode(orderId, request);
    }

    @PostMapping("/orders/{orderId}/cancel")
    public Order cancelOrder(
            @PathVariable String orderId,
            @RequestBody CancelOrderRequest request
    ) {
        return orderService.cancelOrder(orderId, request);
    }

    @GetMapping("/orders/{orderId}/pickup-code")
    public PickupCodeResponse getPickupCode(
            @PathVariable String orderId,
            @RequestParam String shopperId
    ) {
        return orderService.getPickupCode(orderId, shopperId);
    }

    // ===================== RESERVATIONS =====================

    @PostMapping("/reservations")
    public Reservation createReservation(@RequestBody Reservation reservation) {
        return orderService.createReservation(reservation);
    }

    @GetMapping("/reservations")
    public List<Reservation> getReservationsByNgo(@RequestParam String ngoId) {
        return orderService.getReservationsByNgo(ngoId);
    }

    @GetMapping("/reservations/all")
    public List<Reservation> getAllReservations() {
        return orderService.getAllReservations();
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        return orderService.getReservationById(id);
    }

    @PutMapping("/reservations/{id}")
    public Reservation updateReservation(
            @PathVariable String id,
            @RequestBody Reservation reservation
    ) {
        return orderService.updateReservation(id, reservation);
    }

    @DeleteMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservation(@PathVariable String id) {
        orderService.deleteReservation(id);
    }

    @PatchMapping("/reservations/{id}/status")
    public Reservation updateReservationStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return orderService.updateReservationStatus(id, status);
    }

    // ===================== INTERNAL =====================

    @GetMapping("/internal/orders/{orderId}/verify")
    public boolean verifyOrderCompletion(
            @PathVariable String orderId,
            @RequestParam String userId) {

        try {
            Order order = orderService.getOrderById(orderId);
            if (order != null && "COMPLETED".equalsIgnoreCase(order.getStatus().name())) {
                return userId.equals(order.getShopperId()) || userId.equals(order.getSellerUserId());
            }
        } catch (Exception ignored) {
        }

        try {
            Reservation res = orderService.getReservationById(orderId);
            if (res != null && "COMPLETED".equalsIgnoreCase(res.getStatus())) {
                return userId.equals(res.getNgoId()) || userId.equals(res.getRestaurantId());
            }
        } catch (Exception ignored) {
        }

        return false;
    }
}
