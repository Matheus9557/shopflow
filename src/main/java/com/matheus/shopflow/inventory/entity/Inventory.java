package com.matheus.shopflow.inventory.entity;

import com.matheus.shopflow.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "inventory")
public class Inventory extends BaseEntity {

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer availableQuantity;

    @Column(nullable = false)
    private Integer reservedQuantity;

    protected Inventory() {
        // JPA only
    }

    public Inventory(
            Long productId,
            Integer availableQuantity
    ) {
        validateProductId(productId);
        validateInitialQuantity(availableQuantity);

        this.productId = productId;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = 0;
    }

    // =========================
    // DOMAIN BEHAVIOR
    // =========================

    public void addStock(int quantity) {
        validateQuantity(quantity);

        availableQuantity += quantity;
    }

    public void removeStock(int quantity) {
        validateQuantity(quantity);

        if (availableQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient available stock"
            );
        }

        availableQuantity -= quantity;
    }

    public void reserve(int quantity) {
        validateQuantity(quantity);

        if (availableQuantity < quantity) {
            throw new IllegalStateException(
                    "Insufficient stock to reserve"
            );
        }

        availableQuantity -= quantity;
        reservedQuantity += quantity;
    }

    public void release(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException(
                    "Not enough reserved stock to release"
            );
        }

        reservedQuantity -= quantity;
        availableQuantity += quantity;
    }

    public void confirmReservation(int quantity) {
        validateQuantity(quantity);

        if (reservedQuantity < quantity) {
            throw new IllegalStateException(
                    "Not enough reserved stock"
            );
        }

        reservedQuantity -= quantity;
    }

    // =========================
    // DOMAIN VALIDATION
    // =========================

    private void validateProductId(Long productId) {

        if (productId == null || productId <= 0) {
            throw new IllegalArgumentException(
                    "Product id must be greater than zero"
            );
        }
    }

    private void validateInitialQuantity(
            Integer quantity
    ) {

        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException(
                    "Initial stock quantity cannot be negative"
            );
        }
    }

    private void validateQuantity(int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }
    }

    // =========================
    // GETTERS
    // =========================

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