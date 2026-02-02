package com.locally.orders.order_reservationservice.service;

import com.locally.orders.order_reservationservice.client.ListingClient;
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
    private final ListingClient listingClient;

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

    /**
     * Create reservation AND reduce listing quantity by 1 (1 reservation = 1 item)
     */
    public Reservation createReservation(Reservation reservation) {

        // 1️⃣ Ask Listing service for listing quantity
        var listing = listingClient.getListing(reservation.getSurplusItemId());
        if (listing == null) {
            throw new RuntimeException("Listing not found");
        }

        int availableQty = listing.getQuantity() == null ? 0 : listing.getQuantity();

        // 2️⃣ Ensure at least 1 available
        if (availableQty < 1) {
            throw new RuntimeException("No quantity available");
        }

        // 3️⃣ Decrease quantity by 1 in Listing service
        listingClient.updateListingQuantity(
                reservation.getSurplusItemId(),
                availableQty - 1
        );

        // 4️⃣ Save reservation as usual
        return reservationRepository.save(reservation);
    }

    public List<Reservation> getReservationsByNgo(String ngoId) {
        return reservationRepository.findByNgoId(ngoId);
    }

    public Reservation getReservationById(String id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    /**
     * Update reservation status.
     * If CANCELLED, return quantity back to listing-service (+1).
     */
    public Reservation updateReservationStatus(String id, String status) {
        Reservation reservation = getReservationById(id);

        // If cancelling, return 1 item back to listing-service
        if ("CANCELLED".equalsIgnoreCase(status)) {
            var listing = listingClient.getListing(reservation.getSurplusItemId());
            if (listing != null) {
                int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();

                listingClient.updateListingQuantity(
                        reservation.getSurplusItemId(),
                        currentQty + 1
                );
            }
        }

        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
}
