package com.matheus.shopflow.inventory.dto;

public record InventoryRequest(
        Long productId,
        Integer quantity
) {
}