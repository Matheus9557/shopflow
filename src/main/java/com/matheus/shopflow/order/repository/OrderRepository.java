package com.matheus.shopflow.order.repository;

import com.matheus.shopflow.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}