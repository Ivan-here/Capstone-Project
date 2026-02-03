package com.locally.orders.order_reservationservice.service;

import com.locally.orders.order_reservationservice.client.ListingClient;
import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import com.locally.orders.order_reservationservice.repository.OrderRepository;
import com.locally.orders.order_reservationservice.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

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

        // ✅ Reduce stock for FARM_PRODUCT paid orders
        // Treat each item ID in the list as quantity 1.
        if (!order.getItems().isEmpty()) {

            // count duplicates => quantity per listing
            Map<String, Integer> counts = new HashMap<>();
            for (String listingId : order.getItems()) {
                if (listingId != null && !listingId.isBlank()) {
                    counts.put(listingId, counts.getOrDefault(listingId, 0) + 1);
                }
            }

            for (Map.Entry<String, Integer> entry : counts.entrySet()) {
                String listingId = entry.getKey();
                int qtyWanted = entry.getValue();

                var listing = listingClient.getListing(listingId);
                if (listing == null) {
                    throw new RuntimeException("Listing not found: " + listingId);
                }

                // ✅ Must be ACTIVE
                if (listing.getStatus() == null || !"ACTIVE".equalsIgnoreCase(listing.getStatus())) {
                    throw new RuntimeException("Listing is not active: " + listingId + " (status=" + listing.getStatus() + ")");
                }

                // ✅ Must be FARM_PRODUCT for paid orders
                if (listing.getType() == null || !"FARM_PRODUCT".equalsIgnoreCase(listing.getType())) {
                    throw new RuntimeException("This listing cannot be ordered (type=" + listing.getType() + ")");
                }

                int availableQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                if (availableQty < qtyWanted) {
                    throw new RuntimeException("Not enough stock for listing " + listingId +
                            " (wanted=" + qtyWanted + ", available=" + availableQty + ")");
                }

                // decrease quantity
                listingClient.updateListingQuantity(listingId, availableQty - qtyWanted);
            }
        }

        // Default status if not provided
        if (order.getStatus() == null) {
            order.updateStatus(OrderStatus.PENDING, "system");
        } else {
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
     * Only allowed for ACTIVE + SURPLUS_FOOD listings.
     */
    public Reservation createReservation(Reservation reservation) {

        var listing = listingClient.getListing(reservation.getSurplusItemId());
        if (listing == null) {
            throw new RuntimeException("Listing not found");
        }

        // ✅ block CLOSED / OUT_OF_STOCK / anything not ACTIVE
        if (listing.getStatus() == null || !"ACTIVE".equalsIgnoreCase(listing.getStatus())) {
            throw new RuntimeException("Cannot reserve: listing is not ACTIVE (status=" + listing.getStatus() + ")");
        }

        // ✅ reservations only for surplus food
        if (listing.getType() == null || !"SURPLUS_FOOD".equalsIgnoreCase(listing.getType())) {
            throw new RuntimeException("Cannot reserve: listing type must be SURPLUS_FOOD (type=" + listing.getType() + ")");
        }

        int availableQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
        if (availableQty < 1) {
            throw new RuntimeException("No quantity available");
        }

        // decrease quantity by 1
        listingClient.updateListingQuantity(reservation.getSurplusItemId(), availableQty - 1);

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
     * If CANCELLED (and wasn't already cancelled), return quantity back to listing-service (+1).
     */
    public Reservation updateReservationStatus(String id, String status) {
        Reservation reservation = getReservationById(id);

        // ✅ prevent double restock
        if ("CANCELLED".equalsIgnoreCase(status) && !"CANCELLED".equalsIgnoreCase(reservation.getStatus())) {
            var listing = listingClient.getListing(reservation.getSurplusItemId());
            if (listing != null) {
                int currentQty = listing.getQuantity() == null ? 0 : listing.getQuantity();
                listingClient.updateListingQuantity(reservation.getSurplusItemId(), currentQty + 1);
            }
        }

        reservation.setStatus(status);
        return reservationRepository.save(reservation);
    }
}
