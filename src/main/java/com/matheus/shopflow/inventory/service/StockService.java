package com.matheus.shopflow.inventory.service;

import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class StockService {

    private final InventoryRepository repository;

    public StockService(InventoryRepository repository) {
        this.repository = repository;
    }

    public void checkAvailability(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        if (inventory.getAvailableQuantity() < quantity) {
            throw new IllegalStateException("Insufficient stock");
        }
    }

    public void reserve(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.reserve(quantity);
        repository.save(inventory);
    }

    public void release(Long productId, int quantity) {
        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.release(quantity);
        repository.save(inventory);
    }
}