package com.example.listingservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;
import java.util.List;

public record FullUpdateListingDTO(
        @NotBlank(message = "Title cannot be blank")
        String title,

        @PositiveOrZero(message = "Price must be zero or greater")
        Double price,

        String unit,

        String description,

        Integer quantity, // <-- ADDED

        LocalDateTime expiryDate, // <-- ADDED

        List<String> retainedImages
) {}