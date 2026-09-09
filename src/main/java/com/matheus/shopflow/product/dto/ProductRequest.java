package com.matheus.shopflow.product.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ProductRequest(

        @NotBlank(message = "Product name cannot be empty")
        String name,

        String description,

        @NotNull(message = "Product price is required")
        @DecimalMin(
                value = "0.01",
                message = "Product price must be greater than zero"
        )
        BigDecimal price,

        @NotNull(message = "Stock quantity is required")
        @Positive(message = "Stock quantity must be greater than zero")
        Integer stockQuantity
) {
}