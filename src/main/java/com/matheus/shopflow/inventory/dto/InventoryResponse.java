package com.matheus.shopflow.inventory.dto;

public record InventoryResponse(
        Long productId,
        Integer availableQuantity,
        Integer reservedQuantity
) {
}