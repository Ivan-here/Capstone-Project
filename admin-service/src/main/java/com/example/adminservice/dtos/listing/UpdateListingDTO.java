package com.example.adminservice.dtos.listing;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateListingDTO(
        @NotNull(message = "New quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer newQuantity
) {}