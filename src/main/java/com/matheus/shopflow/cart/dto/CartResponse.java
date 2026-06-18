package com.matheus.shopflow.cart.dto;

import com.matheus.shopflow.cart.entity.CartStatus;

import java.math.BigDecimal;

public record CartResponse(
        Long cartId,
        Long customerId,
        CartStatus status,
        Integer totalItems,
        BigDecimal totalAmount
) {
}