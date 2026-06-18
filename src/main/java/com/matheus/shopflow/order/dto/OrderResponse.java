package com.matheus.shopflow.order.dto;

import com.matheus.shopflow.order.entity.OrderStatus;
import java.math.BigDecimal;

public record OrderResponse(
        Long orderId,
        Long customerId,
        OrderStatus status,
        Integer totalItems,
        BigDecimal totalAmount
) {
}