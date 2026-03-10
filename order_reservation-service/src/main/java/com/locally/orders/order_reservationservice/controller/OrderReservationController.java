package com.locally.orders.order_reservationservice.controller;

import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OrderReservationController {

    private final OrderReservationService orderService;

    // ===================== ORDERS =====================

    @PostMapping("/orders")
    public Order createOrder(@RequestBody Order order) {

        if (order.getShopperId() == null ||
                order.getRestaurantId() == null ||
                order.getItems() == null ||
                order.getItems().isEmpty()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "shopperId, restaurantId and items are required"
            );
        }

        return orderService.placeOrder(order);
    }

    @GetMapping("/orders/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Order getOrderById(@PathVariable String id) {
        return orderService.getOrderById(id);
    }

    @PutMapping("/orders/{id}")
    public Order updateOrder(
            @PathVariable String id,
            @RequestBody Order order
    ) {
        return orderService.updateOrder(id, order);
    }
    @DeleteMapping("/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable String id) {
        orderService.deleteOrder(id);
    }

    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(
            @PathVariable String id,
            @RequestParam OrderStatus status
    ) {
        return orderService.updateOrderStatus(id, status);
    }

    @GetMapping("/orders")
    public List<Order> getOrdersByShopper(@RequestParam String shopperId) {
        return orderService.getOrdersByShopper(shopperId);
    }

    @GetMapping("/orders/restaurant/{restaurantId}")
    public List<Order> getOrdersByRestaurant(@PathVariable String restaurantId) {
        return orderService.getOrdersByRestaurant(restaurantId);
    }

    // ===================== RESERVATIONS =====================

    @PostMapping("/reservations")
    public Reservation createReservation(@RequestBody Reservation reservation) {

        if (reservation.getNgoId() == null ||
                reservation.getRestaurantId() == null ||
                reservation.getSurplusItemId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "ngoId, restaurantId and surplusItemId are required"
            );
        }

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

    // ✅ NEW: update reservation status
    @PatchMapping("/reservations/{id}/status")
    public Reservation updateReservationStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return orderService.updateReservationStatus(id, status);
    }
}
