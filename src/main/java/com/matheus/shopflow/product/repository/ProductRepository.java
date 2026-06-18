package com.matheus.shopflow.product.repository;

import com.matheus.shopflow.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}