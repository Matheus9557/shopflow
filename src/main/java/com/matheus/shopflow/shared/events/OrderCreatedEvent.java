package com.matheus.shopflow.shared.events;

public record OrderCreatedEvent(
        Long orderId,
        Long customerId
) {
}