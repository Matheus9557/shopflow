package com.matheus.shopflow.order.entity;

public enum OrderStatus {
    CREATED,
    PAYMENT_PENDING,
    PAID,
    PAYMENT_FAILED,
    CANCELLED,
    SHIPPED,
    DELIVERED
}