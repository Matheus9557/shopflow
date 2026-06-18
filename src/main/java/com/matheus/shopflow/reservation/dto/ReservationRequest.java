package com.matheus.shopflow.reservation.dto;

public record ReservationRequest(
        Long productId,
        Integer quantity
) {}