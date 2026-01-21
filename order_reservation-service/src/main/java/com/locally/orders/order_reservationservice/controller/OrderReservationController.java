package com.locally.orders.order_reservationservice.controller;

import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.service.OrderReservationService;
import com.locally.orders.order_reservationservice.service.OrderReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api") // Base URL for everything in this file
@RequiredArgsConstructor
public class OrderReservationController {

    private final OrderReservationService orderService;

    // ==========================================
    //               ORDER ENDPOINTS
    // ==========================================

    // 1. Shopper places an order
    @PostMapping("/orders")
    @ResponseStatus(HttpStatus.CREATED)
    public String createOrder(@RequestBody Order order) {
        orderService.placeOrder(order);
        return "Order placed successfully! ID: " + order.getId();
    }

    // 2. Restaurant updates status (e.g., PENDING -> CONFIRMED)
    @PatchMapping("/orders/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public String updateOrderStatus(@PathVariable String id, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(id, status);
        return "Order status updated to " + status;
    }

    // 3. Shopper views their history
    // Usage: GET /api/orders?shopperId=user_123
    @GetMapping("/orders")
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getShopperOrders(@RequestParam String shopperId) {
        return orderService.getOrdersForShopper(shopperId);
    }

    // 4. Restaurant views incoming orders (New!)
    // Usage: GET /api/orders/restaurant/rest_999
    @GetMapping("/orders/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Order> getRestaurantOrders(@PathVariable String restaurantId) {
        return orderService.getOrdersForRestaurant(restaurantId);
    }

    // ==========================================
    //            RESERVATION ENDPOINTS
    // ==========================================

    // 5. NGO claims a surplus item
    @PostMapping("/reservations")
    @ResponseStatus(HttpStatus.CREATED)
    public String createReservation(@RequestBody Reservation reservation) {
        orderService.placeReservation(reservation);
        return "Reservation created successfully!";
    }

    // 6. NGO views their claimed items
    // Usage: GET /api/reservations?ngoId=ngo_555
    @GetMapping("/reservations")
    @ResponseStatus(HttpStatus.OK)
    public List<Reservation> getNgoReservations(@RequestParam String ngoId) {
        return orderService.getReservationsForNgo(ngoId);
    }

    // 7. Get specific reservation details
    @GetMapping("/reservations/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Reservation getReservation(@PathVariable String id) {
        return orderService.getReservation(id);
    }
}