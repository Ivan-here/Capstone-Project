package com.example.adminservice.dtos.orderReservation;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "reservations")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Reservation {
    @Id
    private String id;


    private String ngoId;           // The NGO claiming the food
    private String restaurantId;    // The Restaurant giving the food
    private String surplusItemId;   // The specific food item ID (from Listing Service)

    private String status = "RESERVED"; // Default status: RESERVED, PICKED_UP, CANCELLED

    private LocalDateTime reservationTime = LocalDateTime.now(); // Auto-timestamp when created
}