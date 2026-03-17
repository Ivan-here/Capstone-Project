package com.example.adminservice.dtos.listing;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Document(value = "listings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Listing {
    @Id
    private String id;

    private String ownerId;
    private String type; // FARM_PRODUCT or SURPLUS_FOOD
    private String businessName;

    private String title;
    private String description;

    private String category;

    // NEW: Store the AI keywords here!
    // Example: ["Apple", "Fresh", "Red"]
    private List<String> tags;

    // Add this field to your Listing.java class:
    private List<String> imageUrls;

    private BigDecimal price;
    private String unit;
    private Integer quantity;

    private LocalDateTime expiryDate;

    private String pickupLocation;
    private String status; // ACTIVE, OUT_OF_STOCK, EXPIRED

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}