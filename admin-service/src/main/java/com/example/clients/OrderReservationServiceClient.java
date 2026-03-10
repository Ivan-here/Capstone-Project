package com.example.clients;

import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.locally.orders.order_reservationservice.model.Order;

import java.util.List;

@FeignClient(
        name = "orderReservationClient",
        url = "${clients.orderReservationBaseUrl}"
)
public interface OrderReservationServiceClient {

    @PostMapping("/api/orders")
    Order createOrder(@RequestBody Order order);

    @GetMapping("/api/orders/all")
    List<Order> getAllOrders();

    @GetMapping("/api/orders/{id}")
    Order getOrderById(@PathVariable("id") String id);

    @PutMapping("/api/orders/{id}")
    Order updateOrder(
            @PathVariable("id") String id,
            @RequestBody Order order
    );

    @DeleteMapping("/api/orders/{id}")
    void deleteOrder(@PathVariable("id") String id);

    @PatchMapping("/api/orders/{id}/status")
    Order updateOrderStatus(
            @PathVariable("id") String id,
            @RequestParam("status") OrderStatus status
    );

    @GetMapping("/api/orders")
    List<Order> getOrdersByShopper(@RequestParam("shopperId") String shopperId);

    @GetMapping("/api/orders/restaurant/{restaurantId}")
    List<Order> getOrdersByRestaurant(@PathVariable("restaurantId") String restaurantId);


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
