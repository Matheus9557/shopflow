package com.matheus.shopflow.inventory.entity;

import com.matheus.shopflow.shared.model.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    protected Inventory() {}

    public Inventory(Long productId, Integer availableQuantity) {
        if (availableQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    // DOMAIN BEHAVIOR

    public void reserve(int quantity) {
        validateQuantity(quantity);

        if (availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock to reserve");
        }

        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Not enough reserved stock to release");
        }

        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    public void confirmReservation(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException("Not enough reserved stock");
        }

        reservedQuantity -= quantity;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getAvailableQuantity() {
        return availableQuantity;
    }

    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
}