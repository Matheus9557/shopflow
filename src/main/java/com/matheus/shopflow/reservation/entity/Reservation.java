package com.matheus.shopflow.reservation.entity;

import com.matheus.shopflow.shared.model.BaseEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    protected Reservation() {}

    public Reservation(Long productId, Integer quantity, LocalDateTime expiresAt) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        this.productId = productId;
        this.quantity = quantity;
        this.expiresAt = expiresAt;
        this.status = ReservationStatus.ACTIVE;
    }

    public void expire() {
        if (status != ReservationStatus.ACTIVE) {
            return;
        }

        status = ReservationStatus.EXPIRED;
    }

    public void confirm() {
        ensureActive();
        status = ReservationStatus.CONFIRMED;
    }

    public void cancel() {
        ensureActive();
        status = ReservationStatus.CANCELLED;
    }

    public boolean isExpired() {
        return status == ReservationStatus.ACTIVE
                && LocalDateTime.now().isAfter(expiresAt);
    }

    private void ensureActive() {
        if (status != ReservationStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Reservation is not active. Current status: " + status
            );
        }
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public ReservationStatus getStatus() {
        return status;
    }
}