package com.matheus.shopflow.cart.dto;

public record AddItemRequest(
        Long customerId,
        Long productId,
        Integer quantity
) {
}