package com.example.adminservice.clients;

import com.example.adminservice.dtos.orderReservation.OrderStatus;
import com.example.adminservice.dtos.orderReservation.Reservation;
import com.example.adminservice.dtos.orderReservation.AdminDisputeOrderRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.example.adminservice.dtos.orderReservation.Order;

import java.util.List;

@FeignClient(
        name = "orderReservationClient",
        url = "${clients.orderReservationBaseUrl}"
)
public interface OrderReservationServiceClient {

    @GetMapping("/api/orders/all")
    List<Order> getAllOrders();

    @GetMapping("/api/orders/{id}")
    Order getOrderById(@PathVariable("id") String id);

    @DeleteMapping("/api/orders/{id}")
    void deleteOrder(@PathVariable("id") String id);

    @GetMapping("/api/orders")
    List<Order> getOrdersByShopper(@RequestParam("shopperId") String shopperId);

    @GetMapping("/api/orders/seller/{sellerUserId}")
    List<Order> getOrdersBySeller(@PathVariable("sellerUserId") String sellerUserId);

    @PostMapping("/api/orders/{id}/admin-dispute")
    Order adminDisputeOrder(
            @PathVariable("id") String id,
            @RequestBody AdminDisputeOrderRequest request
    );


    // ===================== RESERVATIONS =====================

    @PostMapping("/api/reservations")
    Reservation createReservation(@RequestBody Reservation reservation);

    @GetMapping("/api/reservations/all")
    List<Reservation> getAllReservations();

    @GetMapping("/api/reservations")
    List<Reservation> getReservationsByNgo(@RequestParam("ngoId") String ngoId);

    @GetMapping("/api/reservations/{id}")
    Reservation getReservationById(@PathVariable("id") String id);

    @PutMapping("/api/reservations/{id}")
    Reservation updateReservation(
            @PathVariable("id") String id,
            @RequestBody Reservation reservation
    );

    @DeleteMapping("/api/reservations/{id}")
    void deleteReservation(@PathVariable("id") String id);

    @PatchMapping("/api/reservations/{id}/status")
    Reservation updateReservationStatus(
            @PathVariable("id") String id,
            @RequestParam("status") String status
    );
}
