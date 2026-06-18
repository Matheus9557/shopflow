package com.matheus.shopflow.reservation.dto;

import com.matheus.shopflow.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        Long productId,
        Integer quantity,
        ReservationStatus status,
        LocalDateTime expiresAt
) {}