package com.example.listingservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CreateListingDTO(
        @NotBlank(message = "Owner ID is required")
        String ownerId,

        @NotBlank(message = "Type must be FARM_PRODUCT or SURPLUS_FOOD")
        String type,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotBlank(message = "Category is required")
        String category,

        // MACHINE VISION: This accepts tags like ["Red", "Apple", "Fruit"]
        List<String> tags,

        @NotNull(message = "Price is required")
        @Min(value = 0, message = "Price cannot be negative")
        BigDecimal price,

        @NotBlank(message = "Unit is required (e.g., kg, box)")
        String unit,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        LocalDateTime expiryDate, // Optional (mostly for Surplus)

        String imageUrl // URL to the photo
) {}