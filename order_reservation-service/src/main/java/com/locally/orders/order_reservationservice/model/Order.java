package com.locally.orders.order_reservationservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "orders")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    @Id
    private String id;

    private String shopperId;
    private String restaurantId;
    private List<String> items;  // List of product IDs
    private Double totalPrice;

    private OrderStatus status;

    @Builder.Default // Ensures this defaults to "now" if not provided
    private LocalDateTime orderDate = LocalDateTime.now();

    @Builder.Default // Ensures list is not null
    private List<StatusHistoryItem> history = new ArrayList<>();

    // --- HELPER: Updates status AND history at the same time ---
    public void updateStatus(OrderStatus newStatus, String changedBy) {
        this.status = newStatus;
        this.history.add(new StatusHistoryItem(newStatus, LocalDateTime.now(), changedBy));
    }

    // --- INNER CLASS (StatusHistory) ---
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusHistoryItem {
        private OrderStatus status;
        private LocalDateTime time;
        private String changedBy;
    }
}