package com.matheus.shopflow.product.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record CachedProductResponse(
        Long id,
        String name,
        String description,
        BigDecimal price
) implements Serializable {
}