package com.matheus.shopflow.cart.repository;

import com.matheus.shopflow.cart.entity.Cart;
import com.matheus.shopflow.cart.entity.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomerIdAndStatus(Long customerId, CartStatus status);
}