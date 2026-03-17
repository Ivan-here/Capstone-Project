package com.example.adminservice.controller;

import com.example.adminservice.dtos.orderReservation.Order;
import com.example.adminservice.dtos.orderReservation.Reservation;
import com.example.adminservice.service.AdminOrderReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminOrderReservationController {

    private final AdminOrderReservationService adminOrderReservationService;

    @GetMapping("/orders")
    public List<Order> getAllOrders() {
        return adminOrderReservationService.getAllOrders();
    }

    @GetMapping("/orders/{id}")
    public Order getOrderById(@PathVariable String id) {
        return adminOrderReservationService.getOrderById(id);
    }

    @GetMapping("/orders/shopper/{shopperId}")
    public List<Order> getOrdersByShopper(@PathVariable String shopperId) {
        return adminOrderReservationService.getOrdersByShopper(shopperId);
    }

    @GetMapping("/orders/restaurant/{restaurantId}")
    public List<Order> getOrdersByRestaurant(@PathVariable String restaurantId) {
        return adminOrderReservationService.getOrdersByRestaurant(restaurantId);
    }

    @PatchMapping("/orders/{id}/status")
    public Order updateOrderStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return adminOrderReservationService.updateOrderStatus(id, status);
    }

    @DeleteMapping("/orders/{id}")
    public void deleteOrder(@PathVariable String id) {
        adminOrderReservationService.deleteOrder(id);
    }

    @GetMapping("/reservations")
    public List<Reservation> getAllReservations() {
        return adminOrderReservationService.getAllReservations();
    }

    @GetMapping("/reservations/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        return adminOrderReservationService.getReservationById(id);
    }

    @GetMapping("/reservations/ngo/{ngoId}")
    public List<Reservation> getReservationsByNgo(@PathVariable String ngoId) {
        return adminOrderReservationService.getReservationsByNgo(ngoId);
    }

    @PatchMapping("/reservations/{id}/status")
    public Reservation updateReservationStatus(
            @PathVariable String id,
            @RequestParam String status
    ) {
        return adminOrderReservationService.updateReservationStatus(id, status);
    }

    @DeleteMapping("/reservations/{id}")
    public void deleteReservation(@PathVariable String id) {
        adminOrderReservationService.deleteReservation(id);
    }
}