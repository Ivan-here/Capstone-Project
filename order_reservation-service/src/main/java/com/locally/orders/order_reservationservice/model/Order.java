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

    private OrderStatus status;

    @Builder.Default // Ensures this defaults to "now" if not provided
    private LocalDateTime orderDate = LocalDateTime.now();

    @Builder.Default // Ensures list is not null
    private List<StatusHistoryItem> history = new ArrayList<>();

    // ===== PAYMENT FIELDS =====

    private String shopperId;
    private String sellerUserId;

    private List<OrderItem> items;

    private String pickupLocation;

    private String currency;
    private Long grossAmountCents;
    private Long platformFeeCents;
    private Long sellerAmountCents;
    @Builder.Default
    private boolean requiresPayment = true;

    private PaymentStatus paymentStatus;
    private String stripePaymentIntentId;
    private String stripeClientSecret;
    private String stripeConnectedAccountId;
    private String stripeTransferId;
    private LocalDateTime releasedAt;
    private LocalDateTime refundedAt;
    private String stripeRefundId;

    @Builder.Default
    private boolean stockDeducted = false;

    private String pickupCodeHash;
    private String pickupCodePlain;
    private LocalDateTime pickupCodeExpiresAt;
    private Boolean pickupCodeVerified;
    private LocalDateTime pickupVerifiedAt;
    private String pickupVerifiedBy;
    private LocalDateTime paidAt;
    private LocalDateTime readyForPickupAt;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;

    // --- HELPER: Updates status AND history at the same time ---
    public void updateStatus(OrderStatus newStatus, String changedBy) {
        if (this.history == null) this.history = new ArrayList<>();
        if (this.orderDate == null) this.orderDate = LocalDateTime.now();
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
