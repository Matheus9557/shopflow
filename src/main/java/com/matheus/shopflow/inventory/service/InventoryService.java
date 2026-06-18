package com.matheus.shopflow.inventory.service;

import com.matheus.shopflow.inventory.entity.Inventory;
import com.matheus.shopflow.inventory.repository.InventoryRepository;
import org.springframework.stereotype.Service;

@Service
public class InventoryService {

    private final InventoryRepository repository;

    public InventoryService(InventoryRepository repository) {
        this.repository = repository;
    }

    public Inventory createStock(Long productId, int quantity) {

        Inventory inventory = new Inventory(productId, quantity);

        return repository.save(inventory);
    }

    public void reserveStock(Long productId, int quantity) {

        Inventory inventory = repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Inventory not found"));

        inventory.reserve(quantity);

        repository.save(inventory);
    }
}