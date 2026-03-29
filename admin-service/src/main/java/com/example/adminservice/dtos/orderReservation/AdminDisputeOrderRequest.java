package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminDisputeOrderRequest {
    private String adminUserId;
    private String reason;
    private boolean refundPayment;
}
