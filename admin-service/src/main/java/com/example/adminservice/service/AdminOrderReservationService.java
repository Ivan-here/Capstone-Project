package com.example.adminservice.service;

import com.example.adminservice.clients.OrderReservationServiceClient;
import com.example.adminservice.dtos.orderReservation.AdminDisputeOrderRequest;
import com.example.adminservice.dtos.orderReservation.Order;
import com.example.adminservice.dtos.orderReservation.Reservation;
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

    public List<Order> getOrdersBySeller(String sellerUserId) {
        return orderReservationServiceClient.getOrdersBySeller(sellerUserId);
    }

    public Order disputeOrder(String id, AdminDisputeOrderRequest request) {
        return orderReservationServiceClient.adminDisputeOrder(id, request);
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
