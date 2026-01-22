package com.locally.orders.order_reservationservice.service;

import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.repository.OrderRepository;
import com.locally.orders.order_reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReservationService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    // ================= ORDERS =================

    public Order placeOrder(Order order) {

        // Ensure items list is never null
        if (order.getItems() == null) {
            order.setItems(new ArrayList<>());
        }

        // Default status if not provided
        if (order.getStatus() == null) {
            order.updateStatus(OrderStatus.PENDING, "system");
        } else {
            // Ensure history is initialized even if status came from request
            order.updateStatus(order.getStatus(), "system");
        }

        return orderRepository.save(order);
    }

    public Order updateOrderStatus(String orderId, OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.updateStatus(status, "restaurant");
        return orderRepository.save(order);
    }

    public List<Order> getOrdersByShopper(String shopperId) {
        return orderRepository.findByShopperId(shopperId);
    }

    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    // ================= RESERVATIONS =================

    public Reservation createReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByNgo(String ngoId) {
        return reservationRepository.findByNgoId(ngoId);
    }

    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public Reservation updateReservationStatus(String id, String status) {
        Reservation reservation = getReservationById(id);
        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
}
