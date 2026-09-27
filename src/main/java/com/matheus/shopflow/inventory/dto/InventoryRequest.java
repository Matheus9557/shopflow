package com.matheus.shopflow.inventory.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record InventoryRequest(

        @NotNull(message = "Product id is required")
        @Positive(message = "Product id must be greater than zero")
        Long productId,

        @NotNull(message = "Quantity is required")
        @PositiveOrZero(message = "Quantity cannot be negative")
        Integer quantity
) {
}