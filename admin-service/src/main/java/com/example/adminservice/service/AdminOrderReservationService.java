package com.example.adminservice.service;

import com.example.adminservice.clients.OrderReservationServiceClient;
import com.locally.orders.order_reservationservice.model.Order;
import com.locally.orders.order_reservationservice.model.OrderStatus;
import com.locally.orders.order_reservationservice.model.Reservation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminOrderReservationService {

    private final OrderReservationServiceClient orderReservationServiceClient;

    public List<Order> getAllOrders() {
        return orderReservationServiceClient.getAllOrders();
    }

    public Order getOrderById(String id) {
        return orderReservationServiceClient.getOrderById(id);
    }

    public List<Order> getOrdersByShopper(String shopperId) {
        return orderReservationServiceClient.getOrdersByShopper(shopperId);
    }

    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return orderReservationServiceClient.getOrdersByRestaurant(restaurantId);
    }

    public Order updateOrderStatus(String id, String status) {
        return orderReservationServiceClient.updateOrderStatus(
                id,
                OrderStatus.valueOf(status.toUpperCase())
        );
    }

    public void deleteOrder(String id) {
        orderReservationServiceClient.deleteOrder(id);
    }

    public List<Reservation> getAllReservations() {
        return orderReservationServiceClient.getAllReservations();
    }

    public Reservation getReservationById(String id) {
        return orderReservationServiceClient.getReservationById(id);
    }

    public List<Reservation> getReservationsByNgo(String ngoId) {
        return orderReservationServiceClient.getReservationsByNgo(ngoId);
    }

    public Reservation updateReservationStatus(String id, String status) {
        return orderReservationServiceClient.updateReservationStatus(id, status);
    }

    public void deleteReservation(String id) {
        orderReservationServiceClient.deleteReservation(id);
    }
}