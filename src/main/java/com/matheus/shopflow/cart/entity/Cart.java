package com.matheus.shopflow.cart.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;

    @Enumerated(EnumType.STRING)
    private CartStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "cart_id")
    private List<CartItem> items = new ArrayList<>();

    protected Cart() {}

    public Cart(Long customerId) {
        this.customerId = customerId;
        this.status = CartStatus.ACTIVE;
    }

    public void addItem(CartItem item) {
        items.add(item);
    }

    public BigDecimal total() {
        return items.stream()
                .map(CartItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void checkout() {
        if (items.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }

        this.status = CartStatus.CHECKOUT_PENDING;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public CartStatus getStatus() {
        return status;
    }

    public List<CartItem> getItems() {
        return items;
    }
}