package com.locally.orders.order_reservationservice.service;

import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.repository.OrderRepository;
import com.locally.orders.order_reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderReservationService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    // ==========================================
    //               ORDER LOGIC
    // ==========================================

    public void placeOrder(Order order) {
        if (order.getStatus() == null) {
            order.updateStatus(OrderStatus.PENDING, "System");
        }
        orderRepository.save(order);
    }

    public void updateOrderStatus(String id, OrderStatus newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + id));

        // Use the helper method we made in your Model
        order.updateStatus(newStatus, "Restaurant");

        orderRepository.save(order);
    }

    public List<Order> getOrdersForShopper(String shopperId) {
        return orderRepository.findByShopperId(shopperId);
    }

    public List<Order> getOrdersForRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    // ==========================================
    //            RESERVATION LOGIC
    // ==========================================

    public void placeReservation(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public Reservation getReservation(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found with ID: " + id));
    }

    public void updateReservationStatus(String id, String newStatus) {
        Reservation res = getReservation(id);
        res.setStatus(newStatus);
        reservationRepository.save(res);
    }

    public List<Reservation> getReservationsForNgo(String ngoId) {
        return reservationRepository.findByNgoId(ngoId);
    }
}