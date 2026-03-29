package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private String id;
    private OrderStatus status;

    @Builder.Default
    private LocalDateTime orderDate = LocalDateTime.now();

    @Builder.Default
    private List<StatusHistoryItem> history = new ArrayList<>();

    private String shopperId;
    private String sellerUserId;
    private List<OrderItem> items;
    private String pickupLocation;
    private String currency;
    private Long grossAmountCents;
    private Long platformFeeCents;
    private Long sellerAmountCents;
    private PaymentStatus paymentStatus;
    private String stripePaymentIntentId;
    private String stripeClientSecret;
    private String stripeConnectedAccountId;
    private String stripeTransferId;
    private LocalDateTime releasedAt;
    private LocalDateTime refundedAt;
    private String stripeRefundId;
    private boolean stockDeducted;
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

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusHistoryItem {
        private OrderStatus status;
        private LocalDateTime time;
        private String changedBy;
    }
}
