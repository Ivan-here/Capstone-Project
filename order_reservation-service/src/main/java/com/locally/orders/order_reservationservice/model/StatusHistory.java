package com.locally.orders.order_reservationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@AllArgsConstructor // Generates a constructor with all arguments
@NoArgsConstructor  // Generates an empty constructor
public class StatusHistory {
    private OrderStatus status;
    private LocalDateTime time;
    private String changedBy;
}

